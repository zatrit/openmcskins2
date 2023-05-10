package net.zatrit.skins.lib.api.cache;

import java.io.IOException;

public interface Cache {
    byte[] get(String id, LoadFunction load) throws IOException;

    interface LoadFunction {
        byte[] load() throws IOException;
    }
}
