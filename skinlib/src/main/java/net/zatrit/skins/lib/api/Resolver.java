package net.zatrit.skins.lib.api;

import org.jetbrains.annotations.NotNull;

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
    @NotNull PlayerLoader resolve(Profile profile) throws IOException;

}
