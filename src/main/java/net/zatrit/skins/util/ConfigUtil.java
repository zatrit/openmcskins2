package net.zatrit.skins.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.zatrit.skins.config.ConfigHolder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigUtil {
    public static <T, R> R patchConfig(
            @NotNull ConfigHolder<T> instance,
            @NotNull Function<T, R> callback) {
        final var config = instance.getConfig();
        final var result = callback.apply(config);
        instance.save();

        return result;
    }
}
