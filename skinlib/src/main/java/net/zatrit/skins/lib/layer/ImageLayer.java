package net.zatrit.skins.lib.layer;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.Layer;
import net.zatrit.skins.lib.api.SkinLayer;
import net.zatrit.skins.lib.data.Texture;
import net.zatrit.skins.lib.data.TextureResult;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.function.Function;

@AllArgsConstructor
public class ImageLayer implements SkinLayer {
    private final Collection<Layer<Image>> sublayers;
    private final Collection<TextureType> textureTypes;

    @Override
    @SneakyThrows
    public TextureResult apply(@NotNull TextureResult input) {
        if (!this.textureTypes.contains(input.getType())) {
            return input;
        }

        @Cleanup val stream = new ByteArrayInputStream(input.getTexture()
                                                               .getContent());

        // https://stackoverflow.com/a/44521687/12245612
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        val layers = this.sublayers.stream().map(Layer::function)
                             .reduce(Function::andThen).get();

        val image = (RenderedImage) layers.apply(ImageIO.read(stream));

        @Cleanup val outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);

        val texture = new Texture(
                outputStream.toByteArray(),
                input.getTexture().getMetadata()
        );

        return new TextureResult(texture, input.getType());
    }
}
