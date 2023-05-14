package net.zatrit.skins.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.zatrit.skins.lib.api.cache.CacheProvider;
import net.zatrit.skins.lib.enumtypes.HashFunc;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter
@SuperBuilder
public final class Config {
    private final Gson gson = new GsonBuilder().create();
    private final @Nullable CacheProvider cacheProvider;
    private @Setter HashFunc hashFunc;
    private @Builder.Default Executor executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());
}
