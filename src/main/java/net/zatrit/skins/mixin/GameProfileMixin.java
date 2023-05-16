package net.zatrit.skins.mixin;

import com.mojang.authlib.GameProfile;
import net.zatrit.skins.SkinsClient;
import net.zatrit.skins.lib.api.Profile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.zatrit.skins.lib.util.SneakyLambda.sneaky;

@Mixin(value = GameProfile.class, remap = false)
public abstract class GameProfileMixin implements Profile {
    @Override
    public abstract @Shadow UUID getId();

    @Override
    public abstract @Shadow String getName();

    @Override
    public CompletableFuture<Profile> refreshUuidAsync() {
        return CompletableFuture.supplyAsync(sneaky(this::apiRequest))
                       .thenApply(request -> SkinsClient.getHttpClient()
                                                     .sendAsync(
                                                             request,
                                                             HttpResponse.BodyHandlers.ofInputStream()
                                                     ).join())
                       .thenApply(HttpResponse::body).thenApply(stream -> {
                    final var map = SkinsClient.getConfig().getGson()
                                            .fromJson(
                                                    new InputStreamReader(
                                                            stream), Map.class);

                    final var id = String.valueOf(map.get("id")).replaceAll(
                            "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                            "$1-$2-$3-$4-$5"
                    );

                    return (Profile) new GameProfile(
                            UUID.fromString(id),
                            this.getName()
                    );
                });
    }

    private HttpRequest apiRequest() throws URISyntaxException {
        return HttpRequest.newBuilder().uri(new URI(
                "https://api.mojang.com/users/profiles/minecraft/" +
                        this.getName())).build();
    }
}

