package net.zatrit.skins.lib;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.api.cache.Cache;
import net.zatrit.skins.lib.api.cache.CacheProvider;
import net.zatrit.skins.lib.data.Texture;
import net.zatrit.skins.lib.data.Textures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;

@Getter
@AllArgsConstructor
public class URLPlayerLoader implements Resolver.PlayerLoader {
    private final @Getter(AccessLevel.PROTECTED) CacheProvider cacheProvider;
    private final @NotNull Textures textures;
    private final @NotNull Resolver resolver;


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasTexture(TextureType type) {
        return this.textures.getTextures().containsKey(type) &&
                       this.textures.getTextures().get(type).getUrl() != null;
    }

    /**
     * Download texture from its URL.
     * {@inheritDoc}
     */
    @Override
    public @Nullable Texture download(TextureType type) throws IOException {
        if (!this.hasTexture(type)) {
            return null;
        }

        final var textureData = this.textures.getTextures().get(type);

        return fetchTextureData(
                textureData,
                this.resolver.cacheable() ? this.getCacheProvider() : null
        );
    }

    private @NotNull Texture fetchTextureData(
            @NotNull Textures.TextureData textureData,
            @Nullable CacheProvider cacheProvider) throws IOException {
        final Cache.LoadFunction function = () -> {
            try (var stream = new URL(textureData.getUrl()).openStream()) {
                return stream.readAllBytes();
            }
        };

        final byte[] buffer = cacheProvider != null ?
                                      cacheProvider.getSkinCache()
                                              .getOrLoad(
                                                      textureData.getUrl(),
                                                      function
                                              ) :
                                      function.load();

        return new Texture(buffer, textureData.getMetadata());
    }
}
