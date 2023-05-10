package net.zatrit.skins.mixin;

import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.zatrit.skins.AssetPathProvider;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements AssetPathProvider {
    private @Getter String assetPath;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(@NotNull RunArgs args, CallbackInfo ci) {
        this.assetPath = args.directories.assetDir.getPath();
    }
}
