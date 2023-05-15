package net.zatrit.skins.lib.api;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Profile {
    /**
     * @return player UUID for skin resolution.
     */
    UUID getId();

    /**
     * @return player name.
     */
    String getName();

    /**
     * Asynchronously refreshes UUID from Mojang API or other API.
     */
    CompletableFuture<Profile> refreshUuidAsync();
}
