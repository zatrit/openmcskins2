package net.zatrit.skins.lib.layer;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import lombok.var;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.SkinLayer;
import net.zatrit.skins.lib.data.Texture;
import net.zatrit.skins.lib.data.TextureResult;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;

@AllArgsConstructor
public class ImageLayer implements SkinLayer {
    private final Collection<ImageSublayer> sublayers;
    private final Collection<TextureType> textureTypes;

    @Override
    @SneakyThrows
    public TextureResult apply(@NotNull TextureResult input) {
        if (!this.textureTypes.contains(input.getType())) {
            return input;
        }

        var stream = new ByteArrayInputStream(input.getTexture().getContent());
        var image = ImageIO.read(stream);

        for (val sublayer : this.sublayers) {
            image = sublayer.apply(image);
        }

        val outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);

        val texture = new Texture(
                outputStream.toByteArray(),
                input.getTexture().getMetadata()
        );

        return new TextureResult(texture, input.getType());
    }

    public interface ImageSublayer {
        BufferedImage apply(Image image);
    }
}
