package net.zatrit.skins.lib.layer.awt;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.val;
import net.zatrit.skins.lib.api.Layer;
import net.zatrit.skins.lib.data.TypedTexture;
import net.zatrit.skins.lib.texture.LazyTexture;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * A layer that creates a {@link Image} and applies all layers on it.
 */
@AllArgsConstructor
public class ImageLayer implements Layer<TypedTexture> {
    private final Collection<Layer<BufferedImage>> sublayers;
    private Predicate<TypedTexture> texturePredicate;

    @Override
    public TypedTexture apply(@NotNull TypedTexture input) {
        if (!this.texturePredicate.test(input)) {
            return input;
        }

        val old = input.getTexture();
        val texture = new LazyTexture(old.getId(), old.getMetadata()) {
            @Override
            public byte @NotNull [] getBytes() throws IOException {
                @Cleanup val stream = new ByteArrayInputStream(old.getBytes());

                // https://stackoverflow.com/a/44521687/12245612
                val layers = sublayers.stream().reduce(Layer::andThen)
                    .orElseThrow(NoSuchElementException::new);

                val image = (RenderedImage) layers.apply(ImageIO.read(stream));

                @Cleanup val outputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "png", outputStream);

                return outputStream.toByteArray();
            }
        };
        return new TypedTexture(texture, input.getType());
    }
}
