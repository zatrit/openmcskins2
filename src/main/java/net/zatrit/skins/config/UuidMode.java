package net.zatrit.skins.config;

/** UUID refresh mode. */
public enum UuidMode {
    /** Never refresh UUID. */
    NEVER,
    /** Refresh UUID always before loading the skin. */
    ALWAYS,
    /** Refresh UUID in offline mode only. */
    OFFLINE
}
