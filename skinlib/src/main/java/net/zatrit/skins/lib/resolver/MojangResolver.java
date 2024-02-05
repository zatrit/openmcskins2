package net.zatrit.skins.lib.resolver;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.val;
import net.zatrit.skins.lib.CachedPlayerTextures;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.api.PlayerTextures;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.data.MojangResponse;
import net.zatrit.skins.lib.data.MojangTextures;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Base64;

/**
 * <a href="https://wiki.vg/Mojang_API">Mojang API</a> implementation
 * for OpenMCSkins.
 */
@AllArgsConstructor
public final class MojangResolver implements Resolver {
    private static final String MOJANG_SKIN_API = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private final Config config;

    @Override
    public @NotNull PlayerTextures resolve(@NotNull Profile profile)
        throws IOException {
        val gson = this.config.getGson();
        val url = MOJANG_SKIN_API + profile.getShortId();

        @Cleanup val stream = new URL(url).openStream();
        val response = gson.fromJson(
            new InputStreamReader(stream),
            MojangResponse.class
        );

        val decoder = Base64.getDecoder();
        val textureData = decoder.decode(response.getProperties().get(0)
                                             .getValue());
        @Cleanup
        val bytesReader = new InputStreamReader(new ByteArrayInputStream(
            textureData));

        return new CachedPlayerTextures<>(
            gson.fromJson(
                bytesReader,
                MojangTextures.class
            ).getTextures(),
            this.config.getLayers(),
            this.config.getCacheProvider()
        );
    }
}
