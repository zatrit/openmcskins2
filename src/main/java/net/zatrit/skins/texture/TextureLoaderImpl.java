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
import net.zatrit.skins.lib.api.Texture;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class TextureLoaderImpl implements TextureLoader {
    private final Texture texture;
    private boolean animated = false;

    public static @NotNull TextureLoaderImpl create(@NotNull Texture texture) {
        val metadata = texture.getMetadata();
        val config = new TextureLoaderImpl(texture);

        if (metadata == null) {
            return config;
        }

        config.animated = metadata.isAnimated();

        return config;
    }

    @Override
    public void getTexture(
            @NotNull TextureIdentifier identifier,
            @NotNull Consumer<Identifier> callback) throws IOException {
        val id = identifier.asId();

        @Cleanup
        val stream = new ByteArrayInputStream(this.texture.getBytes());
        val image = NativeImage.read(stream);
        val manager = MinecraftClient.getInstance().getTextureManager();

        AbstractTexture texture;
        if (this.animated) {
            texture = new AnimatedTexture(image, 100);
        } else {
            texture = new NativeImageBackedTexture(image);
        }

        RenderSystem.recordRenderCall(() -> {
            manager.registerTexture(id, texture);
            callback.accept(id);
        });
    }
}
