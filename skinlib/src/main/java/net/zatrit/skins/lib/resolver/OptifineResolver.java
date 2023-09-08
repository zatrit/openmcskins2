package net.zatrit.skins.lib.resolver;

import com.google.common.io.ByteStreams;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.val;
import net.zatrit.skins.lib.BasePlayerLoader;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.PlayerLoader;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.data.Metadata;
import net.zatrit.skins.lib.data.Textures;
import net.zatrit.skins.lib.texture.BytesTexture;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * <a href="https://optifine.readthedocs.io/capes.html">Optifine API</a>
 * resolver for OpenMCSkins.
 * <p>
 * Does not cache skins, because connecting to API already loads textures.
 */
@AllArgsConstructor
public class OptifineResolver implements Resolver {
    private final Config config;
    private final String baseUrl;

    @Override
    public boolean requiresUuid() {
        return false;
    }

    @Override
    public @NotNull PlayerLoader resolve(@NotNull Profile profile)
            throws IOException {
        val textures = new Textures<BytesTexture>();
        val url = new URL(this.baseUrl + "/capes/" + profile.getName() + ".png");
        val connection = (HttpURLConnection) url.openConnection();

        // An easy way to check that the code means OK (2XX).
        if (connection.getResponseCode() / 100 == 2) {
            @Cleanup val stream = connection.getInputStream();
            val content = ByteStreams.toByteArray(stream);
            val texture = new BytesTexture(url.toString(),
                                           content,
                                           new Metadata()
            );

            textures.getTextures().put(TextureType.CAPE, texture);
        }

        /* Since you can't check for the existence/change of a
        texture without fetching that texture, it should not be cached. */
        return new BasePlayerLoader<>(textures, this.config.getLayers());
    }
}
