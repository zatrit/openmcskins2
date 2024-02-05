package net.zatrit.skins.lib.resolver;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.val;
import net.zatrit.skins.lib.BasePlayerTextures;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.PlayerTextures;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.api.Texture;
import net.zatrit.skins.lib.data.MCCapesResponse;
import net.zatrit.skins.lib.data.Metadata;
import net.zatrit.skins.lib.texture.BytesTexture;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Base64;
import java.util.EnumMap;

/**
 * Resolver for <a href="https://minecraftcapes.net/">Minecraft Capes</a>
 * based on the behavior of the Minecraft Capes mod. Connects to the API at
 * {@code https://api.minecraftcapes.net/}
 * <p>
 * Does not cache skins, because connecting to API already loads textures.
 */
@AllArgsConstructor
public final class MinecraftCapesResolver implements Resolver {
    private static final String MINECRAFTCAPES_API = "https://api.minecraftcapes.net/profile/";
    private final Config config;

    @Override
    public @NotNull PlayerTextures resolve(@NotNull Profile profile)
        throws IOException {
        val url = MINECRAFTCAPES_API + profile.getShortId();
        @Cleanup val reader = new InputStreamReader(new URL(url).openStream());
        val response = this.config.getGson().fromJson(
            reader,
            MCCapesResponse.class
        );
        val textures = new EnumMap<TextureType, Texture>(TextureType.class);

        for (val entry : response.getTextures().entrySet()) {
            val type = entry.getKey();
            val textureData = entry.getValue();

            if (textureData == null) {
                continue;
            }

            val metadata = new Metadata();
            val decoder = Base64.getDecoder();

            if (type == TextureType.CAPE) {
                metadata.setAnimated(response.isAnimatedCape());
            }

            val texture = new BytesTexture(
                textureData,
                decoder.decode(textureData),
                metadata
            );

            textures.put(type, texture);
        }

        /* Since you can't resolve a list of textures without
        fetching those textures, they may not be cached */
        return new BasePlayerTextures<>(textures, this.config.getLayers());
    }
}
