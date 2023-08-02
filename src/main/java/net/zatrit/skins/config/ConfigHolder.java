package net.zatrit.skins.config;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

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

    default <R> R patchConfig(@NotNull Function<T, R> callback) {
        val config = this.getConfig();
        val result = callback.apply(config);
        this.save();

        return result;
    }
}
