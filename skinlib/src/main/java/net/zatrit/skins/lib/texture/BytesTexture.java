package net.zatrit.skins.lib.texture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.zatrit.skins.lib.api.RawTexture;

import java.util.Map;

@Getter
@AllArgsConstructor
public class BytesTexture implements RawTexture {
    private String id;
    private byte[] bytes;
    private Map<String, String> metadata;
}
