package net.zatrit.skins.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.Texture;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class TextureLoader {
    private final Texture texture;
    private boolean animated = false;

    public static @NotNull TextureLoader create(@NotNull Texture texture) {
        val metadata = texture.getMetadata();
        val config = new TextureLoader(texture);

        if (metadata == null) {
            return config;
        }

        config.animated = metadata.isAnimated();

        return config;
    }

    public void getTexture(
        @NotNull TextureIdentifier identifier,
        @NotNull Consumer<Identifier> callback) throws IOException {
        val id = identifier.asId();

        @Cleanup val stream = new ByteArrayInputStream(this.texture.getBytes());
        val image = NativeImage.read(stream);
        val manager = MinecraftClient.getInstance().getTextureManager();

        AbstractTexture texture;
        if (this.animated && identifier.getType() == TextureType.CAPE) {
            texture = new AnimatedTexture(image, 100);
        } else if (this.animated) {
            throw new NotImplementedException(
                "Only animated capes are supported.");
        } else {
            texture = new NativeImageBackedTexture(image);
        }

        RenderSystem.recordRenderCall(() -> {
            manager.registerTexture(id, texture);
            callback.accept(id);
        });
    }
}
