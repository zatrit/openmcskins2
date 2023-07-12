package net.zatrit.skins.util;

import dev.isxander.yacl3.config.ConfigInstance;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.zatrit.skins.config.SkinsConfig;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigUtil {
    public static <T, R> R patchConfig(
            @NotNull ConfigInstance<SkinsConfig> instance,
            @NotNull Function<SkinsConfig, R> callback) {
        final var config = instance.getConfig();
        final var result = callback.apply(config);

        instance.save();

        return result;
    }
}
