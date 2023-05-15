package net.zatrit.skins.lib.api.cache;

import java.io.IOException;

public interface Cache {
    /**
     * Loads something from cache if present, else loads using passed function
     */
    byte[] getOrLoad(String id, LoadFunction load) throws IOException;

    interface LoadFunction {
        byte[] load() throws IOException;
    }
}
