package net.zatrit.skins.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigUtil {
    public static <T extends ConfigData, R> R patchConfig(
            @NotNull ConfigHolder<T> holder,
            @NotNull Function<T, R> callback) {
        final var config = holder.get();
        final var result = callback.apply(config);

        holder.save();

        return result;
    }
}
