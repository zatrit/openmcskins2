package net.zatrit.skins.mixin;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.zatrit.skins.HasPath;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements HasPath {
    private @Getter String path;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(@NotNull RunArgs args, CallbackInfo ci) {
        this.path = args.directories.assetDir.getPath();
    }
}
