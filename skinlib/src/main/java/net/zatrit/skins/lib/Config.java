package net.zatrit.skins.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.zatrit.skins.lib.api.cache.CacheProvider;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter
@Builder
public final class Config {
    private final Gson gson = new GsonBuilder().create();
    private @Setter @Nullable CacheProvider cacheProvider;
    private @Setter int loaderTimeout;
    private @Builder.Default Executor executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());
}
