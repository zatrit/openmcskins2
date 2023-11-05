package net.zatrit.skins.accessor;

import net.zatrit.skins.lib.api.Profile;

import java.util.concurrent.CompletableFuture;

public interface AsyncUUIDRefresher {
    /**
     * Asynchronously refreshes UUID from Mojang API or other API.
     */
    CompletableFuture<Profile> skins$refreshUuid();
}
