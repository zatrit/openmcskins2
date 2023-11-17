package net.zatrit.skins;

import lombok.AllArgsConstructor;
import lombok.val;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.IOException;

@AllArgsConstructor
public class ElytraTextureFix
        implements SimpleSynchronousResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return new Identifier("skins", "elytra_texture_fix");
    }

    @Override
    public void reload(@NotNull ResourceManager manager) {
        val elytraId = new Identifier("textures/entity/elytra.png");
        try (val stream = manager.open(elytraId)) {
            val elytraImage = ImageIO.read(stream);
            SkinsClient.getCapeLayer().setElytraTexture(elytraImage);
        } catch (IOException e) {
            SkinsClient.getErrorHandler().accept(e);
        }

        SkinsClient.refresh();
    }
}
