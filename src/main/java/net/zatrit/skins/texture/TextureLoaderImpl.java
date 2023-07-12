package net.zatrit.skins.texture;

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

public class TextureLoaderImpl implements TextureLoader {
    private boolean animated;

    public static @NotNull TextureLoaderImpl fromMetadata(@Nullable Map<String, String> metadata) {
        final var config = new TextureLoaderImpl();

        if (metadata == null) {
            return config;
        }

        config.animated = Boolean.parseBoolean(metadata.getOrDefault("animated",
                "false"
        ));

        return config;
    }

    @Override
    public Identifier getTexture(byte @NotNull [] content) throws IOException {
        if (this.animated) {
            throw new NotImplementedException(
                    "Animated textures aren't supported yet.");
        } else {
            final var image = NativeImage.read(new ByteArrayInputStream(content));
            final var texture = new NativeImageBackedTexture(image);

            return MinecraftClient.getInstance().getTextureManager()
                           .registerDynamicTexture("skins", texture);
        }
    }
}
