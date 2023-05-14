package net.zatrit.skins.mixin;

import com.mojang.authlib.GameProfile;
import lombok.SneakyThrows;
import net.zatrit.skins.SkinsClient;
import net.zatrit.skins.lib.api.Profile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mixin(value = GameProfile.class, remap = false)
public abstract class GameProfileMixin implements Profile {
    @Override
    public abstract @Shadow UUID getId();

    @Override
    public abstract @Shadow String getName();

    @Override
    @SneakyThrows
    public CompletableFuture<Profile> refreshUuidAsync() {
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder().uri(new URI(
                    "https://api.mojang.com/users/profiles/minecraft/" +
                            this.getName())).build();
        } catch (Exception ex) {
            return CompletableFuture.failedFuture(ex);
        }

        return SkinsClient.getHttpClient().sendAsync(request,
                HttpResponse.BodyHandlers.ofInputStream()
        ).thenApply(HttpResponse::body).thenApply(stream -> {
            final var map = SkinsClient.getSkinsConfig().getGson()
                                    .fromJson(new InputStreamReader(stream),
                                            Map.class
                                    );

            final var id = String.valueOf(map.get("id")).replaceAll(
                    "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                    "$1-$2-$3-$4-$5"
            );

            return (Profile) new GameProfile(UUID.fromString(id),
                    this.getName()
            );
        });
    }
}

