package net.zatrit.skins.lib.layer.awt;

import com.google.common.math.IntMath;
import lombok.Setter;
import lombok.val;
import net.zatrit.skins.lib.api.Layer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Calculates the smallest power of the number 2 that
 * is greater than the height of the image and creates
 * a new image with a width equal to the two new heights.
 */
public class ScaleCapeLayer implements Layer<BufferedImage> {
    /**
     * Used to properly render the elytra.
     */
    private @Setter Image elytraTexture;

    @Override
    public BufferedImage apply(@NotNull BufferedImage image) {
        if (image.getHeight() * 2 == image.getWidth()) {
            return image;
        }

        val height = IntMath.ceilingPowerOfTwo(image.getHeight());
        val width = height * 2;

        val result = new BufferedImage(
            width,
            height,
            BufferedImage.TYPE_INT_ARGB
        );

        val graphics = result.getGraphics();
        if (this.elytraTexture != null && image.getWidth() != width) {
            graphics.drawImage(
                this.elytraTexture,
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
