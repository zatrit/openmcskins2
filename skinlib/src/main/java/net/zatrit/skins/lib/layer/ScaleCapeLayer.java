package net.zatrit.skins.lib.layer;

import com.google.common.math.IntMath;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.zatrit.skins.lib.api.Layer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.RoundingMode;

/**
 * Calculates the smallest power of the number 2 that
 * is greater than the height of the image and creates
 * a new image with a width equal to the two new heights.
 */
public class ScaleCapeLayer implements Layer<Image> {
    /**
     * Used to properly render the elytra.
     */
    private @Getter @Setter Image elytraTexture;

    @Override
    public BufferedImage apply(@NotNull Image image) {
        // TODO: use IntMath.ceilingPowerOfTwo when it become stable
        val power = IntMath.log2(image.getHeight(null), RoundingMode.UP);

        val height = IntMath.pow(2, power);
        val width = height * 2;

        val result = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_ARGB
        );

        val graphics = result.getGraphics();
        if (elytraTexture != null && image.getWidth(null) != width) {
            graphics.drawImage(
                    elytraTexture,
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
