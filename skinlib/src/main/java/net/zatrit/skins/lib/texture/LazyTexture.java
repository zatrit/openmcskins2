package net.zatrit.skins.lib.texture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import net.zatrit.skins.lib.api.Texture;
import net.zatrit.skins.lib.util.SneakyLambda;

import java.util.Map;

@AllArgsConstructor
public class LazyTexture implements Texture {
    private @Getter String id;
    private @Getter Map<String, String> metadata;
    private SneakyLambda.SupplierThrows<byte[]> function;

    @Override
    @SneakyThrows
    public byte[] getBytes() {
        return this.function.get();
    }
}
