package net.zatrit.skins.lib.api.cache;

import org.jetbrains.annotations.NotNull;

/**
 * Provides {@link Cache} for skin loader.
 *
 * @see Cache
 */
public interface CacheProvider {
    /**
     * @return non-null cache implementation.
     */
    @NotNull Cache getSkinCache();
}
