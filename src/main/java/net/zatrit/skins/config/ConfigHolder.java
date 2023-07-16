package net.zatrit.skins.config;

import java.util.function.Consumer;

/**
 * An interface similar to ConfigHolder from Cloth Config.
 */
public interface ConfigHolder<T> {
    /**
     * Adds a function that takes a config value
     * as an argument, which is called each time
     * after the {@link #save} method is called.
     */
    void addSaveListener(Consumer<T> listener);

    /**
     * Saves the current configuration
     * depending on the implementation.
     */
    void save();

    void load();

    T getConfig();
}
