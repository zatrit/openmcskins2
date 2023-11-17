package net.zatrit.skins.lib.layer.awt;

import lombok.val;
import net.zatrit.skins.lib.api.Layer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Layer for legacy (64x32) skins to work correctly.
 */
public class LegacySkinLayer implements Layer<Image> {
    @Override
    public Image apply(@NotNull Image input) {
        val size = input.getWidth(null);
        if (input.getHeight(null) != 32) {
            return input;
        }

        val dest = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        val graphics = dest.createGraphics();

        graphics.drawImage(input, 0, 0, null);

        // I'd like to apologize to anyone who understands how
        // AffineTransform works. This code was created by trial
        // and error, so it may be imperfect. If you know how to
        // improve it, feel free to make a pull request.
        drawMirrored(dest, graphics, 0, 20, 16, 52, 12, 12);
        drawMirrored(dest, graphics, 12, 20, 28, 52, 4, 12);
        drawMirrored(dest, graphics, 4, 16, 20, 48, 4, 4);
        drawMirrored(dest, graphics, 8, 16, 24, 48, 4, 4);
        drawMirrored(dest, graphics, 40, 20, 32, 52, 12, 12);
        drawMirrored(dest, graphics, 52, 20, 44, 52, 4, 12);
        drawMirrored(dest, graphics, 44, 16, 36, 48, 4, 4);
        drawMirrored(dest, graphics, 48, 16, 40, 48, 4, 4);

        graphics.dispose();

        return dest;
    }

    private void drawMirrored(
            @NotNull BufferedImage src, @NotNull Graphics2D graphics, int sx,
            int sy, int dx, int dy, int w, int h) {
        graphics.drawImage(
                src.getSubimage(sx, sy, w, h),
                new AffineTransform(-1, 0, 0, 1, dx + w, dy),
                null
        );
    }
}
