package net.zatrit.skins.lib.layer;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.zatrit.skins.lib.api.BufferedImageSublayer;
import net.zatrit.skins.lib.api.SkinLayer;
import net.zatrit.skins.lib.data.Texture;
import net.zatrit.skins.lib.data.TextureResult;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@AllArgsConstructor
public class BufferedImageLayer implements SkinLayer {
    private List<BufferedImageSublayer> sublayers;

    @Override
    @SneakyThrows
    public TextureResult apply(@NotNull TextureResult input) {
        var stream = new ByteArrayInputStream(input.getTexture().getContent());
        var image = ImageIO.read(stream);

        for (final var sublayer : this.sublayers) {
            image = sublayer.apply(image, input);
        }

        final var outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);

        final var texture = new Texture(outputStream.toByteArray(),
                input.getTexture().getMetadata()
        );

        return new TextureResult(texture, input.getType());
    }
}
