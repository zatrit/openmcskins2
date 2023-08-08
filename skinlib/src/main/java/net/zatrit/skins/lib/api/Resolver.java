package net.zatrit.skins.lib.api;

import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.data.TextureResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Interface that describes skin resolving mechanism.
 */
public interface Resolver {
    /**
     * Used for optimization. If all resolvers don't
     * require UUID, skip UUID refreshing.
     */
    default boolean requiresUuid() {
        return true;
    }

    /**
     * @return player-specific texture loader.
     */
    @NotNull Resolver.PlayerLoader resolve(Profile profile) throws IOException;

    /**
     * Player-specific textures loader.
     */
    interface PlayerLoader {
        /**
         * @return true, if texture is present.
         */
        boolean hasTexture(TextureType type);

        /**
         * @return texture of specified type if
         * present (check via {@link #hasTexture}).
         */
        @Nullable TextureResult getTexture(TextureType type) throws IOException;
    }
}
