package net.zatrit.skins.lib.api;

import java.io.IOException;
import java.util.Map;

/**
 * An abstract texture that can be converted to a {@link Byte} array.
 */
public interface Texture {
    /**
     * Texture name used during caching.
     */
    String getId();

    Map<String, String> getMetadata();

    byte[] getBytes() throws IOException;
}
