package net.zatrit.skins.lib;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.zatrit.skins.lib.cache.Cache;
import net.zatrit.skins.lib.cache.CacheProvider;
import net.zatrit.skins.lib.data.Texture;
import net.zatrit.skins.lib.data.Textures;
import net.zatrit.skins.lib.resolver.Resolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;

@AllArgsConstructor
public class TexturesPlayerHandler implements Resolver.PlayerHandler {
    private final @Getter(AccessLevel.PROTECTED) Config skinsConfig;
    private final @NotNull @Getter Textures textures;
    private final @NotNull @Getter Resolver resolver;


    @Override public boolean hasTexture(TextureType type) {
        return this.textures.getTextures().containsKey(type);
    }

    @Override public @Nullable Texture download(TextureType type)
            throws IOException {
        if (!this.hasTexture(type)) {
            return null;
        }

        final var textureData = this.textures.getTextures().get(type);

        return fetchTextureData(textureData, this.resolver.cacheable());
    }

    public @NotNull Texture fetchTextureData(
            @NotNull Textures.TextureData textureData, boolean cache)
            throws IOException {
        final Cache.LoadFunction function = () -> {
            try (var stream = new URL(textureData.getUrl()).openStream()) {
                return stream.readAllBytes();
            }
        };

        final @Nullable CacheProvider cacheProvider = getSkinsConfig().getCacheProvider();

        final byte[] buffer = cache && cacheProvider != null ?
                                      cacheProvider.getSkinCache()
                                              .get(textureData.getUrl(), function) :
                                      function.load();

        return new Texture(buffer, textureData.getMetadata());
    }
}
