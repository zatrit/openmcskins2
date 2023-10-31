package net.zatrit.skins.lib.layer;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.val;
import net.zatrit.skins.lib.api.Layer;
import net.zatrit.skins.lib.api.SkinLayer;
import net.zatrit.skins.lib.data.TypedTexture;
import net.zatrit.skins.lib.texture.LazyTexture;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * A layer that creates a {@link Image} and applies all layers on it.
 */
@AllArgsConstructor
public class ImageLayer implements SkinLayer {
    private final Collection<Layer<Image>> sublayers;
    private Predicate<TypedTexture> texturePredicate;

    @Override
    public TypedTexture apply(@NotNull TypedTexture input) {
        if (!this.texturePredicate.test(input)) {
            return input;
        }

        val texture = input.getTexture();
        return new TypedTexture(new LazyTexture(
                texture.getId(),
                texture.getMetadata(),
                () -> {
                    @Cleanup
                    val stream = new ByteArrayInputStream(
                            texture.getBytes());

                    // https://stackoverflow.com/a/44521687/12245612
                    val layers = this.sublayers.stream()
                            .reduce(Layer::andThen)
                            .orElseThrow(NoSuchElementException::new);

                    val image = (RenderedImage) layers.apply(
                            ImageIO.read(stream));

                    @Cleanup
                    val outputStream = new ByteArrayOutputStream();
                    ImageIO.write(
                            image,
                            "png",
                            outputStream
                    );

                    return outputStream.toByteArray();
                }
        ), input.getType());
    }
}
