package net.zatrit.skins.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import net.zatrit.skins.SkinsClient;
import net.zatrit.skins.lib.api.Profile;
import org.apache.http.client.methods.HttpGet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.zatrit.skins.lib.util.SneakyLambda.sneaky;

@Mixin(value = GameProfile.class, remap = false)
public abstract class GameProfileMixin implements Profile {
    @Shadow
    public abstract PropertyMap getProperties();

    @Override
    @SneakyThrows
    public CompletableFuture<Profile> refreshUuidAsync() {
        val url = "https://api.mojang.com/users/profiles/minecraft/" +
                          URLEncoder.encode(this.getName(), "UTF8");
        val request = new HttpGet(new URI(url));

        return CompletableFuture.supplyAsync(
                sneaky(() -> SkinsClient.getHttpClient().execute(request)),
                SkinsClient.getLoaderConfig().getExecutor()
        ).thenApply(sneaky(response -> {
            val content = response.getEntity().getContent();
            @Cleanup val reader = new InputStreamReader(content);
            val map = SkinsClient.getLoaderConfig().getGson().fromJson(
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
        }));
    }
}

