package net.zatrit.skins.lib.resolver;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.val;
import net.zatrit.skins.lib.CachedPlayerTextures;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.api.PlayerTextures;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.data.MojangTextures;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Valhalla Skin Server Resolver.
 * Read more <a href="https://skins.minelittlepony-mod.com/docs">here</a>.
 */
@AllArgsConstructor
public final class ValhallaResolver implements Resolver {
    private final Config config;
    private final String baseUrl;

    @Override
    public @NotNull PlayerTextures resolve(@NotNull Profile profile)
            throws IOException {
        val url = this.baseUrl + profile.getId();
        @Cleanup val stream = new URL(url).openStream();

        return new CachedPlayerTextures<>(
                this.config.getGson()
                        .fromJson(
                                new InputStreamReader(
                                        stream),
                                MojangTextures.class
                        ).getTextures(),
                this.config.getLayers(),
                this.config.getCacheProvider()
        );
    }
}
