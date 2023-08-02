package net.zatrit.skins.lib;

import lombok.val;
import net.zatrit.skins.lib.api.Texture;
import net.zatrit.skins.lib.api.cache.Cache;
import net.zatrit.skins.lib.api.cache.CacheProvider;
import net.zatrit.skins.lib.data.Textures;
import net.zatrit.skins.lib.texture.LazyTexture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CachedPlayerLoader<T extends Texture> extends BasePlayerLoader<T> {
    private final @Nullable Cache cache;

    public CachedPlayerLoader(
            @Nullable CacheProvider cacheProvider,
            @NotNull Textures<T> textures) {
        super(textures);
        this.cache = cacheProvider != null ? cacheProvider.getSkinCache() : null;
    }

    /**
     * Download texture from its URL.
     * {@inheritDoc}
     */
    @Override
    protected Texture wrapTexture(@NotNull T sourceTexture) {
        val texture = super.wrapTexture(sourceTexture);

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
