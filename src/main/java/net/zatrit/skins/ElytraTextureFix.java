package net.zatrit.skins;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.zatrit.skins.lib.api.SkinLayer;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.IOException;

@AllArgsConstructor
public class ElytraTextureFix implements SimpleSynchronousResourceReloadListener {
    private final @Getter Identifier fabricId = new Identifier(
            "skins",
            "elytra_texture_fix"
    );

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void reload(@NotNull ResourceManager manager) {
        val elytraId = new Identifier("textures/entity/elytra.png");

        try (val stream = manager.getAllResources(elytraId).stream().findFirst()
                                  .get().getInputStream()) {
            val elytraImage = ImageIO.read(stream);
            SkinLayer.CAPE_LAYER.setElytraTexture(elytraImage);
        } catch (IOException e) {
            SkinsClient.getErrorHandler().accept(e);
        }

        SkinsClient.refresh();
    }
}
