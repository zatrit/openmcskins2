package net.zatrit.skins.lib;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.zatrit.skins.lib.api.cache.CacheProvider;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public final class Config {
    private final Gson gson = new Gson();
    private @Setter CacheProvider cacheProvider;
    @SuppressWarnings("CanBeFinal")
    private Executor executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());
}
