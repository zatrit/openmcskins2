package net.zatrit.skins.lib.api;

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
            Collections.singleton(TextureType.CAPE)
    ));
}
