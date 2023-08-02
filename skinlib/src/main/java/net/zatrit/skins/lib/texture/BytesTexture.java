package net.zatrit.skins.lib.texture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.zatrit.skins.lib.api.Texture;

import java.util.Map;

@Getter
@AllArgsConstructor
public class BytesTexture implements Texture {
    private String id;
    private byte[] bytes;
    private Map<String, String> metadata;
}
