package net.zatrit.skins.lib.api;

import com.google.common.collect.Lists;
import lombok.val;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.data.TypedTexture;
import net.zatrit.skins.lib.layer.ImageLayer;
import net.zatrit.skins.lib.layer.LegacySkinLayer;
import net.zatrit.skins.lib.layer.ScaleCapeLayer;

import java.util.Collection;
import java.util.Collections;

/**
 * A layer that somehow changes the {@link TypedTexture}.
 */
public interface SkinLayer extends Layer<TypedTexture> {
    /**
     * Layer used to fix the resolution of static capes.
     */
    ScaleCapeLayer CAPE_LAYER = new ScaleCapeLayer();

    /**
     * List of layers that can be used by default
     * for correct rendering of capes and other fixes.
     */
    Collection<SkinLayer> DEFAULT_LAYERS = Lists.newArrayList(new ImageLayer(
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
    ), new ImageLayer(
            Collections.singleton(new LegacySkinLayer()),
            texture -> texture.getType() == TextureType.SKIN
    ));
}
