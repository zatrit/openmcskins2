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
import net.zatrit.skins.lib.data.Metadata;
import net.zatrit.skins.lib.texture.BytesTexture;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;

@AllArgsConstructor
public final class FiveZigResolver implements Resolver {
    private static final String FIVEZIG_API = "https://textures.5zigreborn.eu/profile/";
    private final Config config;

    @Override
    public @NotNull PlayerTextures resolve(@NotNull Profile profile)
            throws IOException {
        val url = FIVEZIG_API + profile.getId().toString();
        @Cleanup val reader = new InputStreamReader(new URL(url).openStream());
        val response = this.config.getGson().fromJson(reader, Map.class);

        val textures = new EnumMap<TextureType, BytesTexture>(TextureType.class);
        val textureData = (String) response.get("d");

        if (textureData != null) {
            val metadata = new Metadata();
            val decoder = Base64.getDecoder();

            val texture = new BytesTexture(
                    textureData,
                    decoder.decode(textureData),
                    metadata
            );

            textures.put(TextureType.CAPE, texture);
        }

        /* Since you can't resolve a list of textures without
        fetching those textures, they may not be cached */
        return new BasePlayerTextures<>(textures, this.config.getLayers());
    }
}
