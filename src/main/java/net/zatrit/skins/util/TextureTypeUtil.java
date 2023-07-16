package net.zatrit.skins.util;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.zatrit.skins.lib.TextureType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TextureTypeUtil {
    /**
     * Converts texture type from {@link TextureType}
     * to {@link MinecraftProfileTexture.Type}.
     */
    @Contract(pure = true)
    public static MinecraftProfileTexture.@Nullable Type toAuthlibType(@NotNull TextureType type) {
        return switch (type) {
            case SKIN -> MinecraftProfileTexture.Type.SKIN;
            case CAPE -> MinecraftProfileTexture.Type.CAPE;
            default -> null;
        };
    }
}
