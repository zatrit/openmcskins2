package net.zatrit.skins.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.zatrit.skins.lib.TextureType;

public class TextureTypeUtil {
    @Contract(pure = true)
    public static MinecraftProfileTexture.@Nullable Type toAuthlibType(@NotNull TextureType type) {
        switch (type) {
            case SKIN:
                return MinecraftProfileTexture.Type.SKIN;
            case CAPE:
                return MinecraftProfileTexture.Type.CAPE;
            default:
                return null;
        }
    }
}
