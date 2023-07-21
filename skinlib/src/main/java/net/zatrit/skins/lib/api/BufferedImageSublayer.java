package net.zatrit.skins.lib.api;

import net.zatrit.skins.lib.data.TextureResult;

import java.awt.image.BufferedImage;
import java.util.function.Function;

public interface BufferedImageSublayer {
    BufferedImage apply(BufferedImage image, TextureResult input);
}
