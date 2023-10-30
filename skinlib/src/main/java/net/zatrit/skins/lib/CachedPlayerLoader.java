package net.zatrit.skins.lib;

import lombok.val;
import net.zatrit.skins.lib.api.PlayerLoader;
import net.zatrit.skins.lib.api.SkinLayer;
import net.zatrit.skins.lib.api.Texture;
import net.zatrit.skins.lib.api.cache.Cache;
import net.zatrit.skins.lib.api.cache.CacheProvider;
import net.zatrit.skins.lib.data.Textures;
import net.zatrit.skins.lib.texture.LazyTexture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Implementation of {@link PlayerLoader} with caching support with
 * {@link CacheProvider}.
 *
 * @param <T> texture type.
 * @see PlayerLoader
 */
public class CachedPlayerLoader<T extends Texture> extends BasePlayerLoader<T> {
    private final @Nullable Cache cache;

    public CachedPlayerLoader(
            @NotNull Textures<T> textures, @NotNull Collection<SkinLayer> layers,
            @Nullable CacheProvider cacheProvider) {
        super(textures, layers);
        this.cache = cacheProvider != null ? cacheProvider.getSkinCache() : null;
    }

    @Override
    protected Texture wrapTexture(@NotNull T sourceTexture) {
        val texture = super.wrapTexture(sourceTexture);

        // If there is no cache, it returns the texture in its original form.
        if (this.cache == null) {
            return texture;
        }

        return new LazyTexture(
                texture.getId(),
                texture.getMetadata(),
                () -> this.cache.getOrLoad(texture.getId(), texture::getBytes)
        );
    }
}
