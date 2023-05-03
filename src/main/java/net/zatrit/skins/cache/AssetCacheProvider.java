package net.zatrit.skins.cache;

import lombok.Getter;
import net.zatrit.skins.lib.cache.Cache;
import net.zatrit.skins.lib.cache.CacheProvider;

import net.zatrit.skins.HasPath;

@Getter
public class AssetCacheProvider implements CacheProvider {
    private final Cache skinCache;

    public AssetCacheProvider(HasPath path) {
        this.skinCache = new AssetCache(path, "skins");
    }
}
