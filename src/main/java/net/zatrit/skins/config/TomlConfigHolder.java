package net.zatrit.skins.config;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import dev.isxander.yacl3.config.ConfigInstance;
import lombok.Getter;
import lombok.val;
import net.zatrit.skins.SkinsClient;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * TOML implementation for {@link ConfigInstance} and {@link ConfigHolder}.
 */
public class TomlConfigHolder<T> extends ConfigInstance<T> implements ConfigHolder<T> {
    private final Collection<Consumer<T>> listeners = new ArrayList<>();
    private final @Getter File file;
    private final @Getter T defaults;

    @SuppressWarnings("unchecked")
    public TomlConfigHolder(File file, @NotNull T defaultConfig) {
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
        val tomlWriter = new TomlWriter();
        val config = this.getConfig();

        try {
            tomlWriter.write(config, this.file);
        } catch (IOException e) {
            SkinsClient.getErrorHandler().accept(e);
        }

        this.listeners.forEach(listener -> listener.accept(config));
    }

    @Override
    public void load() {
        val toml = new Toml();

        try {
            val config = toml.read(this.file).to(this.getConfigClass());
            this.setConfig(config);
        } catch (Exception e) {
            SkinsClient.getErrorHandler().accept(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSaveListener(Consumer<T> listener) {
        this.listeners.add(listener);
    }
}
