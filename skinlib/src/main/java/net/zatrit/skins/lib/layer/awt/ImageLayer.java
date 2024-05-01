package net.zatrit.skins.lib.layer.awt;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.val;
import lombok.var;
import net.zatrit.skins.lib.api.Layer;
import net.zatrit.skins.lib.data.TypedTexture;
import net.zatrit.skins.lib.texture.LazyTexture;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A layer that creates a {@link Image} and applies all layers on it.
 */
@AllArgsConstructor
public abstract class ImageLayer implements Layer<TypedTexture> {
    @Override
    public final @NotNull TypedTexture apply(@NotNull TypedTexture input) {
        val old = input.getTexture();

        if (!this.accepts(input)) {
            return input;
        }

        val texture = new LazyTexture(old.getId(), old.getMetadata()) {
            @Override
            public byte @NotNull [] getBytes() throws IOException {
                @Cleanup val stream = new ByteArrayInputStream(old.getBytes());

                var image = apply(ImageIO.read(stream));

                @Cleanup val outputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "png", outputStream);

                return outputStream.toByteArray();
            }
        };
        return new TypedTexture(texture, input.getType());
    }

    protected abstract BufferedImage apply(BufferedImage image);

    protected boolean accepts(@NotNull TypedTexture input) {
        return true;
    }
}
