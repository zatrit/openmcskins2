package net.zatrit.skins.lib.resolver;

import com.google.gson.reflect.TypeToken;
import lombok.*;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.URLPlayerLoader;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.data.Textures;
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
public class NamedHTTPResolver implements Resolver {
    private final transient @Getter(AccessLevel.PROTECTED) Config config;
    private final @Getter String baseUrl;

    /**
     * Doesn't require UUID, because resolves by name.
     * {@inheritDoc}
     */
    @Override
    public boolean requiresUuid() {
        return false;
    }

    @Override
    public @NotNull Resolver.PlayerLoader resolve(@NotNull Profile profile)
            throws IOException {
        val url = new URL(this.getBaseUrl() + profile.getName());
        val config = getConfig();

        @Cleanup val reader = new InputStreamReader(url.openStream());

        // Type for EnumMap<TextureType, Textures.TextureData>
        val type = TypeToken.getParameterized(EnumMap.class,
                TextureType.class,
                Textures.TextureData.class
        ).getType();

        val textures = new Textures(this.config.getGson()
                                            .fromJson(reader, type));

        return new URLPlayerLoader(config.getCacheProvider(), textures, this);
    }
}
