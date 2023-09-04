package net.zatrit.skins.config;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import lombok.*;
import net.zatrit.skins.SkinsClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * TOML implementation for {@link ConfigHolder}.
 */
@RequiredArgsConstructor
public class TomlConfigHolder<T> implements ConfigHolder<T> {
    private final Collection<Consumer<T>> listeners = new ArrayList<>();
    private final @Getter Path file;
    private final @Getter T defaults;
    private final @Getter Class<T> configClass;
    private @Getter @Setter T config;

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
            @Cleanup val stream = Files.newOutputStream(file);
            tomlWriter.write(config, stream);
        } catch (IOException e) {
            SkinsClient.getErrorHandler().accept(e);
        }

        this.listeners.forEach(listener -> listener.accept(config));
    }

    @Override
    public void load() {
        val toml = new Toml();

        try {
            @Cleanup val stream = Files.newInputStream(this.file);
            val config = toml.read(stream).to(this.getConfigClass());
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
