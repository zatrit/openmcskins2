package net.zatrit.skins.util;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import lombok.experimental.UtilityClass;
import net.zatrit.skins.lib.TextureType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class TextureTypeUtil {
    /**
     * Converts texture type from {@link TextureType}
     * to {@link MinecraftProfileTexture.Type}.
     */
    public static MinecraftProfileTexture.@Nullable Type toAuthlibType(
            @NotNull TextureType type) {
        switch (type) {
            case SKIN:
                return MinecraftProfileTexture.Type.SKIN;
            case CAPE:
                return MinecraftProfileTexture.Type.CAPE;
            default:
                return null;
        }
    }

    /**
     * Converts texture type from {@link MinecraftProfileTexture.Type}
     * to {@link TextureType}.
     */
    @Contract(pure = true)
    public static @Nullable TextureType fromAuthlibType(
            MinecraftProfileTexture.@NotNull Type type) {
        switch (type) {
            case SKIN:
                return TextureType.SKIN;
            case CAPE:
                return TextureType.CAPE;
            default:
                return null;
        }
    }
}
