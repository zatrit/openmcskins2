package net.zatrit.skins.lib.api;

import lombok.val;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.data.TextureResult;
import net.zatrit.skins.lib.layer.ImageLayer;
import net.zatrit.skins.lib.layer.ScaleCapeLayer;

import java.util.Collection;
import java.util.Collections;

public interface SkinLayer extends Layer<TextureResult> {
    ScaleCapeLayer CAPE_LAYER = new ScaleCapeLayer();

    Collection<SkinLayer> DEFAULT_LAYERS = Collections.singleton(new ImageLayer(
            Collections.singleton(CAPE_LAYER),
            // Applies only to static cape textures.
            texture -> {
                val metadata = texture.getTexture().getMetadata();
                val cape = texture.getType() == TextureType.CAPE;

                if (metadata == null) {
                    return cape;
                }

                return cape && !metadata.isAnimated();
            }
    ));
}
