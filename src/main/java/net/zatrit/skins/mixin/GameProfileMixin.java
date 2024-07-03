package net.zatrit.skins.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import net.zatrit.skins.SkinsClient;
import net.zatrit.skins.accessor.AsyncUUIDRefresher;
import net.zatrit.skins.lib.api.Profile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mixin(value = GameProfile.class, remap = false)
public abstract class GameProfileMixin implements Profile, AsyncUUIDRefresher {
    @Shadow
    public abstract PropertyMap getProperties();

    @Override
    public CompletableFuture<Profile> skins$refreshUuid() {
        return CompletableFuture.supplyAsync(() -> {
            @Cleanup val reader = new InputStreamReader(apiRequest());
            val map = SkinsClient.getSkinlibConfig().getGson().fromJson(
                reader,
                Map.class
            );

            val id = String.valueOf(map.get("id")).replaceAll(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                "$1-$2-$3-$4-$5"
            );

            val profile = new GameProfile(UUID.fromString(id), this.getName());
            profile.getProperties().putAll(this.getProperties());

            return (Profile) profile;
        });
    }

    @Unique
    @SneakyThrows
    private InputStream apiRequest() {
        val request = HttpRequest.newBuilder().uri(new URI(
                "https://api.mojang.com/users/profiles/minecraft/" + this.getName()))
            .build();

        return SkinsClient.getHttpClient().sendAsync(
            request,
            HttpResponse.BodyHandlers.ofInputStream()
        ).join().body();
    }
}

