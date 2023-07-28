package net.zatrit.skins.lib.api;

import com.google.common.collect.Lists;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.data.TextureResult;
import net.zatrit.skins.lib.layer.ImageLayer;
import net.zatrit.skins.lib.layer.ScaleCapeLayer;

import java.util.Collection;

public interface SkinLayer extends Layer<TextureResult> {
    ScaleCapeLayer CAPE_LAYER = new ScaleCapeLayer();

    Collection<SkinLayer> DEFAULT_LAYERS = Lists.newArrayList(new ImageLayer(
            Lists.newArrayList(CAPE_LAYER),
            Lists.newArrayList(TextureType.CAPE)
    ));
}
