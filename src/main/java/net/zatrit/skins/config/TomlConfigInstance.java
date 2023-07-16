package net.zatrit.skins.config;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import dev.isxander.yacl3.config.ConfigInstance;
import lombok.Getter;
import net.zatrit.skins.SkinsClient;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * TOML implementation for {@link ConfigInstance} and {@link ConfigHolder}.
 */
public class TomlConfigInstance<T> extends ConfigInstance<T> implements ConfigHolder<T> {
    private final List<Consumer<T>> listeners = new ArrayList<>();
    private final @Getter File file;
    private final @Getter T defaults;

    @SuppressWarnings("unchecked")
    public TomlConfigInstance(File file, @NotNull T defaultConfig) {
        super((Class<T>) defaultConfig.getClass());

        this.file = file;
        this.defaults = defaultConfig;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation saves a config of
     * type T to a file stored in {@link #file}.
     */
    @Override
    public void save() {
        final var tomlWriter = new TomlWriter();
        final var config = this.getConfig();

        try {
            tomlWriter.write(config, this.file);
        } catch (IOException e) {
            SkinsClient.getErrorHandler().accept(e);
        }

        this.listeners.forEach(listener -> listener.accept(config));
    }

    @Override
    public void load() {
        final var toml = new Toml();
        final var config = toml.read(this.file).to(this.getConfigClass());

        this.setConfig(config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSaveListener(Consumer<T> listener) {
        this.listeners.add(listener);
    }
}
