package net.zatrit.skins.cache;

import lombok.Getter;
import net.zatrit.skins.AssetPathProvider;
import net.zatrit.skins.lib.api.cache.Cache;
import net.zatrit.skins.lib.api.cache.CacheProvider;

@Getter
public class AssetCacheProvider implements CacheProvider {
    /**
     * Cache provider for game asset directory.
     * {@inheritDoc}
     */
    private final Cache skinCache;

    public AssetCacheProvider(AssetPathProvider path) {
        this.skinCache = new AssetCache(path, "skins");
    }
}
