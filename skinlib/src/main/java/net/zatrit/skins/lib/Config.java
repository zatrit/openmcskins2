package net.zatrit.skins.lib;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
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
    @Setter
    private @Nullable CacheProvider cacheProvider;
    @Setter
    private @Builder.Default HashFunction hashFunction = Hashing.murmur3_128();
    private @Builder.Default Executor executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());
}
