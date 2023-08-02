package net.zatrit.skins.lib;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.zatrit.skins.lib.api.cache.CacheProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public final class Config {
    private @Nullable CacheProvider cacheProvider;
    private @NotNull Gson gson = new Gson();
    private @NotNull Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime()
                                                                              .availableProcessors());
}
