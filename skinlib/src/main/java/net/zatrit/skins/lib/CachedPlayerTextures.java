package net.zatrit.skins.lib;

import lombok.val;
import net.zatrit.skins.lib.api.PlayerTextures;
import net.zatrit.skins.lib.api.SkinLayer;
import net.zatrit.skins.lib.api.Texture;
import net.zatrit.skins.lib.api.cache.Cache;
import net.zatrit.skins.lib.api.cache.CacheProvider;
import net.zatrit.skins.lib.texture.LazyTexture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * Implementation of {@link PlayerTextures} with caching support with
 * {@link CacheProvider}.
 *
 * @param <T> texture type.
 * @see PlayerTextures
 */
public class CachedPlayerTextures<T extends Texture>
        extends BasePlayerTextures<T> {
    private final @Nullable Cache cache;

    public CachedPlayerTextures(
            @NotNull Map<TextureType, T> map,
            @NotNull Collection<SkinLayer> layers,
            @Nullable CacheProvider cacheProvider) {
        super(map, layers);
        this.cache = cacheProvider != null ? cacheProvider.getSkinCache() : null;
    }

    @Override
    protected Texture wrapTexture(@NotNull T sourceTexture) {
        val texture = super.wrapTexture(sourceTexture);

        // If there is no cache, it returns the texture in its original form.
        if (this.cache == null) {
            return texture;
        }

        return new LazyTexture(texture.getId(), texture.getMetadata()) {
            @Override
            public byte[] getBytes() {
                return cache.getOrLoad(texture.getId(), texture::getBytes);
            }
        };
    }
}
