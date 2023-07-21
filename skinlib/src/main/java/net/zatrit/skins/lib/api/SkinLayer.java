package net.zatrit.skins.lib.api;

import com.google.common.collect.ImmutableList;
import net.zatrit.skins.lib.data.TextureResult;
import net.zatrit.skins.lib.layer.BufferedImageLayer;
import net.zatrit.skins.lib.layer.ScaleCapeLayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.function.Function;

public interface SkinLayer {
    TextureResult apply(TextureResult result);

    @Contract(pure = true)
    static @NotNull @Unmodifiable Collection<SkinLayer> defaultLayers() {
        return ImmutableList.of(new BufferedImageLayer(ImmutableList.of(new ScaleCapeLayer())));
    }

    default Function<TextureResult, TextureResult> function() {
        return this::apply;
    }
}
