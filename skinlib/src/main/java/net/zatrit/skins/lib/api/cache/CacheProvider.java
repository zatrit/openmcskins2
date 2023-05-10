package net.zatrit.skins.lib.api.cache;

import org.jetbrains.annotations.NotNull;

public interface CacheProvider {
    @NotNull
    Cache getSkinCache();
}
