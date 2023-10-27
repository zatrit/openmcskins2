package net.zatrit.skins.config;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.ConfigSerializer;
import dev.isxander.yacl3.config.v2.api.FieldAccess;
import lombok.Cleanup;
import lombok.Getter;
import lombok.val;
import net.zatrit.skins.SkinsClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * TOML implementation for {@link ConfigSerializer}.
 */
public class TomlConfigSerializer<T> extends ConfigSerializer<T> {
    private final Collection<Consumer<T>> listeners = new ArrayList<>();
    private final @Getter Path file;

    public TomlConfigSerializer(Path file, ConfigClassHandler<T> config) {
        super(config);
        this.file = file;
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
        val config = this.config.instance();

        try {
            @Cleanup val stream = Files.newOutputStream(file);
            tomlWriter.write(config, stream);
        } catch (IOException e) {
            SkinsClient.getErrorHandler().accept(e);
        }

        this.listeners.forEach(listener -> listener.accept(config));
    }

    @SuppressWarnings("unchecked")
    @Override
    public LoadResult loadSafely(Map<ConfigField<?>, FieldAccess<?>> bufferAccessMap) {
        try {
            @Cleanup val stream = Files.newInputStream(file);
            val configClass = this.config.configClass();
            val instance = new Toml().read(stream).to(configClass);

            for (val access : bufferAccessMap.values()) {
                val field = configClass.getDeclaredField(access.name());
                val fieldAccess = ((FieldAccess<Object>) access);
                field.setAccessible(true);

                fieldAccess.set(field.get(instance));
            }
        } catch (IOException | ReflectiveOperationException e) {
            SkinsClient.getErrorHandler().accept(e);
            return LoadResult.FAILURE;
        }

        return LoadResult.SUCCESS;
    }

    public void addSaveListener(Consumer<T> listener) {
        this.listeners.add(listener);
    }
}
