package net.zatrit.skins.lib.resolver;

import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.val;
import net.zatrit.skins.lib.CachedPlayerTextures;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.PlayerTextures;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.texture.URLTexture;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.EnumMap;

/**
 * <a href="https://docs.ely.by/en/skins-system.html">ely.by API</a> implementation
 * for OpenMCSKins. Works for some other APIs.
 */
@AllArgsConstructor
public final class NamedHTTPResolver implements Resolver {
    private final Config config;
    private final String baseUrl;

    /**
     * Doesn't require UUID, because resolves by name.
     * {@inheritDoc}
     */
    @Override
    public boolean requiresUuid() {
        return false;
    }

    @Override
    public @NotNull PlayerTextures resolve(@NotNull Profile profile)
        throws IOException {
        val url = new URL(this.baseUrl + profile.getName());
        @Cleanup val reader = new InputStreamReader(url.openStream());

        // Type for EnumMap<TextureType, URLTexture>
        val type = TypeToken.getParameterized(
            EnumMap.class,
            TextureType.class,
            URLTexture.class
        ).getType();

        return new CachedPlayerTextures<>(
            this.config.getGson().fromJson(reader, type),
            this.config.getLayers(),
            this.config.getCacheProvider()
        );
    }
}
