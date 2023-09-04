package net.zatrit.skins.lib.resolver;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.val;
import net.zatrit.skins.lib.CachedPlayerLoader;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.api.PlayerLoader;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.data.Textures;
import net.zatrit.skins.lib.texture.URLTexture;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Valhalla Skin Server Resolver.
 * Read more <a href="https://skins.minelittlepony-mod.com/docs">here</a>.
 */
@AllArgsConstructor
public class ValhallaResolver implements Resolver {
    private final Config config;
    private final String baseUrl;

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull PlayerLoader resolve(@NotNull Profile profile)
            throws IOException {
        val url = this.baseUrl + profile.getId();
        val gson = this.config.getGson();
        @Cleanup val stream = new URL(url).openStream();

        return new CachedPlayerLoader<>(
                this.config.getCacheProvider(),
                this.config.getLayers(),
                (Textures<URLTexture>) gson.fromJson(
                        new InputStreamReader(stream),
                        MojangResolver.URL_TEXTURES
                )
        );
    }
}
