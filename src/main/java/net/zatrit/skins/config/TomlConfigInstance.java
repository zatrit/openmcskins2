package net.zatrit.skins.config;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import dev.isxander.yacl3.config.ConfigInstance;
import lombok.Getter;
import net.zatrit.skins.SkinsClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TomlConfigInstance<T> extends ConfigInstance<T> {
    private final List<Consumer<T>> listeners = new ArrayList<>();
    private final @Getter File file;
    private final T defaultConfig;

    public TomlConfigInstance(
            File file, Class<T> configClass, T defaultConfig) {
        super(configClass);

        this.file = file;
        this.defaultConfig = defaultConfig;
    }

    @Override
    public T getDefaults() {
        return this.defaultConfig;
    }

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

        this.listeners.forEach(listener -> listener.accept(config));
    }

    public void addUpdateListener(Consumer<T> listener) {
        this.listeners.add(listener);
    }
}
