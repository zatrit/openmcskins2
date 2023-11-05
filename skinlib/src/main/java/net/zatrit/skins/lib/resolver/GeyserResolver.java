package net.zatrit.skins.lib.resolver;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.val;
import lombok.var;
import net.zatrit.skins.lib.CachedPlayerTextures;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.api.PlayerTextures;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.data.MojangTextures;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * An implementation of the GeyserMC skin API based on
 * <a href="https://github.com/onebeastchris/customplayerheads">CustomPlayerHeads</a>.
 */
@AllArgsConstructor
public final class GeyserResolver implements Resolver {
    private static final String GEYSER_XUID_API = "https://api.geysermc.org/v2/xbox/xuid/";
    private static final String GEYSER_SKIN_API = "https://api.geysermc.org/v2/skin/";
    private final Config config;
    private final Collection<String> floodgatePrefixes;

    @Override
    public boolean requiresUuid() {
        return false;
    }

    @Override
    public @NotNull PlayerTextures resolve(@NotNull Profile profile)
            throws IOException, NoSuchElementException {
        var name = profile.getName();
        val prefix = floodgatePrefixes.stream().filter(name::startsWith)
                .findFirst().orElseThrow(NoSuchElementException::new);
        name = name.substring(prefix.length());

        val xuidUrl = new URL(GEYSER_XUID_API + name);
        val xuid = (Integer) config.getGson().fromJson(new InputStreamReader(
                xuidUrl.openStream()), Map.class).get("xuid");

        val skinUrl = new URL(GEYSER_SKIN_API + xuid);
        /* value contains literally the same data as properties[0] in the
         Mojang API response, so it can be decoded in the same way */
        val response = (String) config.getGson().fromJson(new InputStreamReader(
                skinUrl.openStream()), Map.class).get("value");

        val decoder = Base64.getDecoder();
        val textureData = decoder.decode(response);
        @Cleanup val reader = new InputStreamReader(new ByteArrayInputStream(
                textureData));

        return new CachedPlayerTextures<>(
                config.getGson().fromJson(
                        reader,
                        MojangTextures.class
                ).getTextures(),
                this.config.getLayers(),
                this.config.getCacheProvider()
        );
    }
}
