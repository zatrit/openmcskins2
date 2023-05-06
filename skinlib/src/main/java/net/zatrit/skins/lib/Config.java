package net.zatrit.skins.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.zatrit.skins.lib.cache.CacheProvider;
import net.zatrit.skins.lib.enumtypes.HashFunc;
import org.jetbrains.annotations.Nullable;

@SuperBuilder
@Getter
public final class Config {
    private final Gson gson = new GsonBuilder().create();
    private final @Nullable CacheProvider cacheProvider;
    private @Setter HashFunc hashFunc;
}
