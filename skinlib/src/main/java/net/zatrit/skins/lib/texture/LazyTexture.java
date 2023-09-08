package net.zatrit.skins.lib.texture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import net.zatrit.skins.lib.api.Texture;
import net.zatrit.skins.lib.data.Metadata;
import net.zatrit.skins.lib.util.SneakyLambda;

/**
 * A texture that loads its content using a given
 * {@link net.zatrit.skins.lib.util.SneakyLambda.SupplierThrows}.
 */
@AllArgsConstructor
public class LazyTexture implements Texture {
    private final @Getter String id;
    private final @Getter Metadata metadata;
    private final SneakyLambda.SupplierThrows<byte[]> function;

    @Override
    @SneakyThrows
    public byte[] getBytes() {
        return this.function.get();
    }
}
