package net.zatrit.skins.lib;

import java.util.UUID;

public interface Profile {
    UUID getId();

    String getName();

    void refreshUuid();
}
