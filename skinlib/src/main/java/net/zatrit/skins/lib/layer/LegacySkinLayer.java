package net.zatrit.skins.lib.layer;

import lombok.val;
import net.zatrit.skins.lib.api.Layer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Layer for legacy skins to work correctly.
 */
public class LegacySkinLayer implements Layer<Image> {
    @Override
    public Image apply(@NotNull Image input) {
        val size = input.getWidth(null);
        if (size / input.getHeight(null) != 2) {
            return input;
        }

        val dest = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        val graphics = dest.createGraphics();
        val transform = new AffineTransform();

        graphics.drawImage(input, 0, 0, null);

        transform.setToTranslation(16, 48);
        graphics.drawImage(dest.getSubimage(0, 16, 16, 16), transform, null);

        transform.setToTranslation(32, 48);
        graphics.drawImage(dest.getSubimage(40, 16, 16, 16), transform, null);

        return dest;
    }

}
