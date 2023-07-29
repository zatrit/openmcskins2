package net.zatrit.skins.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.zatrit.skins.lib.data.Texture;
import org.apache.commons.lang3.NotImplementedException;
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

        config.animated = Boolean.parseBoolean(metadata.getOrDefault(
                "animated",
                "false"
        ));

        return config;
    }

    @Override
    public void getTexture(
            @NotNull TextureIdentifier identifier,
            @NotNull Consumer<Identifier> callback) throws IOException {
        val id = identifier.asId();
        if (this.animated) {
            throw new NotImplementedException(
                    "Animated textures aren't supported yet.");
        } else {
            val image = NativeImage.read(new ByteArrayInputStream(this.texture.getContent()));
            val texture = new NativeImageBackedTexture(image);
            val manager = MinecraftClient.getInstance().getTextureManager();

            RenderSystem.recordRenderCall(() -> {
                manager.registerTexture(id, texture);
                callback.accept(id);
            });
        }
    }
}
