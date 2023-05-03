package net.zatrit.skins.lib.cache;

import org.jetbrains.annotations.NotNull;

public interface CacheProvider {
    @NotNull
    Cache getSkinCache();
}
