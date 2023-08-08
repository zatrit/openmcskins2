package net.zatrit.skins.lib.resolver;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.val;
import net.zatrit.skins.lib.BasePlayerLoader;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.PlayerLoader;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.data.MCCapesResponse;
import net.zatrit.skins.lib.data.Metadata;
import net.zatrit.skins.lib.data.Textures;
import net.zatrit.skins.lib.texture.BytesTexture;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Base64;
import java.util.EnumMap;

@AllArgsConstructor
public class MinecraftCapesResolver implements Resolver {
    private static final String BASE_URL = "https://api.minecraftcapes.net/profile/";
    private final Config config;

    @Override
    public @NotNull PlayerLoader resolve(@NotNull Profile profile)
            throws IOException {
        val url = BASE_URL + profile.getId().toString().replace("-", "");
        @Cleanup val reader = new InputStreamReader(new URL(url).openStream());
        val response = this.config.getGson().fromJson(reader,
                                                      MCCapesResponse.class
        );
        val textures = new Textures<BytesTexture>(new EnumMap<>(TextureType.class));

        for (val type : TextureType.values()) {
            val typeName = type.toString().toLowerCase();
            val textureData = response.getTextures().get(typeName);

            if (textureData != null) {
                val metadata = new Metadata();
                val decoder = Base64.getDecoder();

                if (type == TextureType.CAPE) {
                    metadata.setAnimated(response.isAnimatedCape());
                }

                val texture = new BytesTexture(textureData,
                                               decoder.decode(textureData),
                                               metadata
                );

                textures.getTextures().put(type, texture);
            }
        }

        /* Since you can't resolve a list of textures without
        fetching those textures, they may not be cached */
        return new BasePlayerLoader<>(textures, this.config.getLayers());
    }
}
