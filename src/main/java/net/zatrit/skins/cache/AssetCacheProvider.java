package net.zatrit.skins.cache;

import lombok.Getter;
import net.zatrit.skins.accessor.HasAssetPath;
import net.zatrit.skins.lib.api.cache.Cache;
import net.zatrit.skins.lib.api.cache.CacheProvider;

/**
 * Cache provider for game asset directory.
 * {@inheritDoc}
 */
@Getter
public class AssetCacheProvider implements CacheProvider {
    private final Cache skinCache;

    public AssetCacheProvider(HasAssetPath path) {
        this.skinCache = new AssetCache(path, "omcs");
    }
}
