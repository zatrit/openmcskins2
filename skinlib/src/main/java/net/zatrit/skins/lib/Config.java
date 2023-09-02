package net.zatrit.skins.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.zatrit.skins.lib.api.SkinLayer;
import net.zatrit.skins.lib.api.cache.CacheProvider;
import net.zatrit.skins.lib.util.AnyCaseEnumDeserializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter
@Setter
@NoArgsConstructor
public final class Config {
    private @Nullable CacheProvider cacheProvider;
    private @NotNull Collection<SkinLayer> layers = SkinLayer.DEFAULT_LAYERS;
    // Very cool Gson that parses TextureType ignoring case
    private @NotNull Gson gson = new GsonBuilder().registerTypeAdapter(
            TextureType.class,
            new AnyCaseEnumDeserializer<>()
    ).create();
    private @NotNull Executor executor = Executors.newFixedThreadPool(
            // All available processors are used, because parallelism is cool.
            Runtime.getRuntime().availableProcessors());
}
