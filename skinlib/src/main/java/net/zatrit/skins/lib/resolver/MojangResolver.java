package net.zatrit.skins.lib.resolver;

import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.val;
import net.zatrit.skins.lib.CachedPlayerLoader;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.api.PlayerLoader;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.data.MojangResponse;
import net.zatrit.skins.lib.data.Textures;
import net.zatrit.skins.lib.texture.URLTexture;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Base64;

/**
 * <a href="https://wiki.vg/Mojang_API">Mojang API</a> implementation
 * for OpenMCSkins.
 */
@AllArgsConstructor
public final class MojangResolver implements Resolver {
    public static final Type URL_TEXTURES = TypeToken.getParameterized(
            Textures.class,
            URLTexture.class
    ).getType();
    private final Config config;

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull PlayerLoader resolve(@NotNull Profile profile)
            throws IOException {
        val gson = this.config.getGson();
        val url = "https://sessionserver.mojang.com/session/minecraft/profile/" +
                          profile.getId().toString().replaceAll("-", "");
        System.out.println(url);

        MojangResponse response;
        @Cleanup val stream = new URL(url).openStream();

        response = gson.fromJson(
                new InputStreamReader(stream),
                MojangResponse.class
        );

        val decoder = Base64.getDecoder();
        val textureData = decoder.decode(response.getProperties().get(0)
                                                 .getValue());
        @Cleanup
        val bytesReader = new InputStreamReader(new ByteArrayInputStream(
                textureData));

        return new CachedPlayerLoader<>(
                this.config.getCacheProvider(),
                this.config.getLayers(),
                (Textures<URLTexture>) gson.fromJson(
                        bytesReader,
                        URL_TEXTURES
                )
        );
    }
}
