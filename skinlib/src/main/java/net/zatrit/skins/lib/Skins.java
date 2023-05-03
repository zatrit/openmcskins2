package net.zatrit.skins.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.zatrit.skins.lib.cache.Cache;
import net.zatrit.skins.lib.cache.CacheProvider;
import net.zatrit.skins.lib.data.Texture;
import net.zatrit.skins.lib.data.Textures;
import net.zatrit.skins.lib.enumtypes.HashFunc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;

@SuperBuilder
@Getter
public final class Skins {
    private final SkinLoader skinLoader = new SkinLoader();
    private final Gson gson = new GsonBuilder().create();
    private final @Nullable CacheProvider cacheProvider;
    private @Setter HashFunc hashFunc;

    public @NotNull Texture fetchTextureData(Textures.@NotNull TextureData textureData,
            boolean cache) throws IOException {
        final Cache.LoadFunction function = () -> {
            try (var stream = new URL(textureData.getUrl()).openStream()) {
                return stream.readAllBytes();
            }
        };

        final byte[] buffer = cache && getCacheProvider() != null
                ? getCacheProvider().getSkinCache().get(textureData.getUrl(), function)
                : function.load();

        return new Texture(buffer, textureData.getMetadata());
    }
}
