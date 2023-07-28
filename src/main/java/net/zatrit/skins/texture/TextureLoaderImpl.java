package net.zatrit.skins.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class TextureLoaderImpl implements TextureLoader {
    private final Identifier id;
    private boolean animated = false;

    public static @NotNull TextureLoaderImpl create(
            @NotNull TextureIdentifier id,
            @Nullable Map<String, String> metadata) {
        val config = new TextureLoaderImpl(id.asId());

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
            byte @NotNull [] content, Consumer<Identifier> callback)
            throws IOException {
        if (this.animated) {
            throw new NotImplementedException(
                    "Animated textures aren't supported yet.");
        } else {
            val image = NativeImage.read(new ByteArrayInputStream(content));
            val texture = new NativeImageBackedTexture(image);
            val manager = MinecraftClient.getInstance().getTextureManager();

            RenderSystem.recordRenderCall(() -> {
                manager.registerTexture(id, texture);
                callback.accept(id);
            });
        }
    }
}
