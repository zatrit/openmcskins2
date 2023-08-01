package net.zatrit.skins.lib.texture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import net.zatrit.skins.lib.api.RawTexture;
import net.zatrit.skins.lib.util.SneakyLambda;

import java.util.Map;

@Getter
@AllArgsConstructor
public class LazyTexture implements RawTexture {
    private String id;
    private Map<String, String> metadata;
    private SneakyLambda.SupplierThrows<byte[]> function;

    @Override
    @SneakyThrows
    public byte[] getBytes() {
        return this.function.get();
    }
}
