package net.zatrit.skins.lib.layer;

import com.google.common.math.IntMath;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.BufferedImageSublayer;
import net.zatrit.skins.lib.data.TextureResult;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.math.RoundingMode;

/**
 * Calculates the smallest power of the number 2 that
 * is greater than the height of the image and creates
 * a new image with a width equal to the two new heights.
 */
public class ScaleCapeLayer implements BufferedImageSublayer {
    @Override
    public BufferedImage apply(
            BufferedImage image,
            @NotNull TextureResult input) {
        if (input.getType() != TextureType.CAPE) {
            return image;
        }

        final var heightLog2 = IntMath.log2(
                image.getHeight(),
                RoundingMode.CEILING
        );

        final var height = IntMath.pow(2, heightLog2);
        final var width = height * 2;

        final var result = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_ARGB
        );

        final var graphics = result.getGraphics();
        graphics.drawImage(image, 0, 0, null);

        return result;
    }
}
