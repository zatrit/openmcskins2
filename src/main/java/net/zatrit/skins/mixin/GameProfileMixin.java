package net.zatrit.skins.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import net.zatrit.skins.SkinsClient;
import net.zatrit.skins.accessor.AsyncUUIDRefresher;
import zatrit.skins.lib.api.Profile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Mixin(value = GameProfile.class, remap = false)
public abstract class GameProfileMixin implements Profile, AsyncUUIDRefresher {
    @Shadow
    public abstract PropertyMap getProperties();

    @Override
    public CompletableFuture<Profile> skins$refreshUuid() {
        return CompletableFuture.supplyAsync(new Supplier<Profile>() {
            @Override
            @SneakyThrows
            public Profile get() {
                val url = new URL(
                    "https://api.mojang.com/users/profiles/minecraft/" +
                        GameProfileMixin.this.getName());

                @Cleanup val reader = new InputStreamReader(url.openStream());
                val map = SkinsClient.getSkinlibConfig().getGson().fromJson(
                    reader,
                    Map.class
                );

                val id = String.valueOf(map.get("id")).replaceAll(
                    "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                    "$1-$2-$3-$4-$5"
                );

                val profile = new GameProfile(
                    UUID.fromString(id),
                    GameProfileMixin.this.getName()
                );
                profile.getProperties()
                    .putAll(GameProfileMixin.this.getProperties());

                return (Profile) profile;
            }
        });
    }
}

