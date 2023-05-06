package net.zatrit.skins.cache;

import lombok.Getter;
import net.zatrit.skins.HasPath;
import net.zatrit.skins.lib.cache.Cache;
import net.zatrit.skins.lib.cache.CacheProvider;

@Getter
public class AssetCacheProvider implements CacheProvider {
    private final Cache skinCache;

    public AssetCacheProvider(HasPath path) {
        this.skinCache = new AssetCache(path, "skins");
    }
}
