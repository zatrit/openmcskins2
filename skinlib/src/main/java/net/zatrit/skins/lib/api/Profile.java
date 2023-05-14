package net.zatrit.skins.lib.api;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Profile {
    UUID getId();

    String getName();

    CompletableFuture<Profile> refreshUuidAsync();
}
