package net.zatrit.skins.lib.api.cache;

import java.io.IOException;

/**
 * @see CacheProvider
 */
public interface Cache {
    /**
     * Loads bytes from cache if present, else loads using passed function.
     */
    byte[] getOrLoad(String id, LoadFunction load) throws IOException;

    interface LoadFunction {
        byte[] load() throws IOException;
    }
}
