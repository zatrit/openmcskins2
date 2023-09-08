package net.zatrit.skins.util;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import lombok.experimental.UtilityClass;
import net.zatrit.skins.lib.TextureType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public final class TextureTypeUtil {
    /**
     * Converts texture type from {@link TextureType}
     * to {@link MinecraftProfileTexture.Type}.
     */
    public static MinecraftProfileTexture.@Nullable Type toAuthlibType(@NotNull TextureType type) {
        return switch (type) {
            case SKIN -> MinecraftProfileTexture.Type.SKIN;
            case CAPE -> MinecraftProfileTexture.Type.CAPE;
            default -> null;
        };
    }

    /**
     * Converts texture type from {@link MinecraftProfileTexture.Type}
     * to {@link TextureType}.
     */
    public static TextureType fromAuthlibType(MinecraftProfileTexture.@NotNull Type type) {
        return switch (type) {
            case SKIN -> TextureType.SKIN;
            case CAPE -> TextureType.CAPE;
            default -> null;
        };
    }
}
