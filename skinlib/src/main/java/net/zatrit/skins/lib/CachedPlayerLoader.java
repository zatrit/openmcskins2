package net.zatrit.skins.lib;

import lombok.Getter;
import lombok.val;
import net.zatrit.skins.lib.api.RawTexture;
import net.zatrit.skins.lib.api.cache.Cache;
import net.zatrit.skins.lib.api.cache.CacheProvider;
import net.zatrit.skins.lib.data.Textures;
import net.zatrit.skins.lib.texture.LazyTexture;
import org.jetbrains.annotations.NotNull;

public class CachedPlayerLoader<T extends RawTexture> extends BasePlayerLoader<T> {
    private final @Getter Cache cache;

    public CachedPlayerLoader(
            @NotNull CacheProvider cacheProvider,
            @NotNull Textures<T> textures) {
        super(textures);
        this.cache = cacheProvider.getSkinCache();
    }

    /**
     * Download texture from its URL.
     * {@inheritDoc}
     */
    @Override
    protected RawTexture wrapTexture(@NotNull T sourceTexture) {
        val texture = super.wrapTexture(sourceTexture);

        return new LazyTexture(
                texture.getId(),
                texture.getMetadata(),
                () -> getCache().getOrLoad(texture.getId(), texture::getBytes)
        );
    }
}
