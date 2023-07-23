package net.zatrit.skins.lib.api;

import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.data.TextureResult;
import net.zatrit.skins.lib.layer.ImageLayer;
import net.zatrit.skins.lib.layer.ScaleCapeLayer;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public interface SkinLayer {
    Collection<SkinLayer> defaultLayers = List.of(new ImageLayer(
            List.of(new ScaleCapeLayer()),
            List.of(TextureType.CAPE)
    ));

    TextureResult apply(TextureResult result);

    default Function<TextureResult, TextureResult> function() {
        return this::apply;
    }
}
