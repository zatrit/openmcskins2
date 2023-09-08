package net.zatrit.skins.mixin;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.zatrit.skins.accessor.HasMetadata;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = MinecraftProfileTexture.class, remap = false)
public abstract class MinecraftProfileTextureMixin implements HasMetadata {
    public abstract @Accessor Map<String, String> getMetadata();
}
