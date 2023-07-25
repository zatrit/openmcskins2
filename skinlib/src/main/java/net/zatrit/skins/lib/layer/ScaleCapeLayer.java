package net.zatrit.skins.lib.layer;

import com.google.common.math.IntMath;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.RoundingMode;

/**
 * Calculates the smallest power of the number 2 that
 * is greater than the height of the image and creates
 * a new image with a width equal to the two new heights.
 */
public class ScaleCapeLayer implements ImageLayer.ImageSublayer {
    /**
     * Used to properly render the elytra.
     */
    private @Getter @Setter Image backgroundTexture;

    @Override
    public BufferedImage apply(@NotNull Image image) {
        // TODO: use IntMath.ceilingPowerOfTwo when it become stable
        final var power = IntMath.log2(image.getHeight(null), RoundingMode.UP);

        final var height = IntMath.pow(2, power);
        final var width = height * 2;

        final var result = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_ARGB
        );

        final var graphics = result.getGraphics();
        if (backgroundTexture != null) {
            graphics.drawImage(
                    backgroundTexture,
                    0,
                    0,
                    result.getWidth(),
                    result.getHeight(),
                    null
            );
        }
        graphics.drawImage(image, 0, 0, null);

        return result;
    }
}
