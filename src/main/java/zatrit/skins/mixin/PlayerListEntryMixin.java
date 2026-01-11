package zatrit.skins.mixin;

import com.mojang.authlib.GameProfile;
import java.util.function.Supplier;
import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerListEntry.class)
public class PlayerListEntryMixin {
  @Shadow @Final private GameProfile profile;
  @Shadow @Final @Mutable private Supplier<SkinTextures> texturesSupplier;

  @Redirect(
      method = "<init>",
      at =
          @At(
              value = "FIELD",
              target =
                  "Lnet/minecraft/client/network/PlayerListEntry;texturesSupplier:Ljava/util/function/Supplier;"))
  private void customTexturesSupplier(PlayerListEntry instance, Supplier<SkinTextures> unused) {
    val provider = MinecraftClient.getInstance().getSkinProvider();

    texturesSupplier =
        () ->
            provider.fetchSkinTextures(profile).getNow(DefaultSkinHelper.getSkinTextures(profile));
  }
}
