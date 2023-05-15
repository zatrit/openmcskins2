package net.zatrit.skins.lib.api;

import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.data.Texture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public interface Resolver {
    /**
     * Used for optimization. If all resolvers don't
     * require UUID, skip UUID refreshing.
     */
    default boolean requiresUuid() {
        return true;
    }

    /**
     * @return true if resolver doesn't fetch remote skins.
     */
    default boolean cacheable() {
        return true;
    }

    @NotNull PlayerHandler resolve(Profile profile) throws IOException;

    interface PlayerHandler {
        /**
         * @return true, if texture is present.
         */
        boolean hasTexture(TextureType type);

        /**
         * @return texture of specified type as Texture if
         * present (check via {@link #hasTexture})).
         */
        @Nullable Texture download(TextureType type) throws IOException;
    }
}
