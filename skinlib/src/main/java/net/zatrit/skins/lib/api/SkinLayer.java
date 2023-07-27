package net.zatrit.skins.lib.api;

import com.google.common.collect.Lists;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.data.TextureResult;
import net.zatrit.skins.lib.layer.ImageLayer;
import net.zatrit.skins.lib.layer.ScaleCapeLayer;

import java.util.Collection;
import java.util.function.Function;

public interface SkinLayer {
    ScaleCapeLayer CAPE_LAYER = new ScaleCapeLayer();

    Collection<SkinLayer> DEFAULT_LAYERS = Lists.newArrayList(new ImageLayer(
            Lists.newArrayList(CAPE_LAYER),
            Lists.newArrayList(TextureType.CAPE)
    ));

    TextureResult apply(TextureResult result);

    default Function<TextureResult, TextureResult> function() {
        return this::apply;
    }
}
