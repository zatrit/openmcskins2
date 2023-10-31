package net.zatrit.skins.lib.texture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.zatrit.skins.lib.api.Texture;
import net.zatrit.skins.lib.data.Metadata;

/**
 * A texture that loads its content using a given
 * {@link net.zatrit.skins.lib.util.SneakyLambda.SupplierThrows}.
 */
@Getter
@AllArgsConstructor
public abstract class LazyTexture implements Texture {
    private final String id;
    private final Metadata metadata;
}
