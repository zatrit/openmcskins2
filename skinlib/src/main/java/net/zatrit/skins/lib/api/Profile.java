package net.zatrit.skins.lib.api;

import java.util.UUID;

public interface Profile {
    UUID getId();

    String getName();

    void refreshUuid();
}
