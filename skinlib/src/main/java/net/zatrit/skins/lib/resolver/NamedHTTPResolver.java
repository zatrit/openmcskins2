package net.zatrit.skins.lib.resolver;

import com.google.gson.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Resolver.PlayerLoader resolve(@NotNull Profile profile)
            throws IOException {
        final var url = new URL(this.getBaseUrl() +
                                        "/textures/" +
                                        profile.getName());
        final var config = getConfig();

        final var type = new TypeToken<EnumMap<TextureType, Textures.TextureData>>() {}.getType();
        final var textures = new Textures(config.getGson()
                                                  .fromJson(
                                                          new InputStreamReader(
                                                                  url.openStream()),
                                                          type
                                                  ));

        return new URLPlayerLoader(config.getCacheProvider(), textures, this);
    }
}
