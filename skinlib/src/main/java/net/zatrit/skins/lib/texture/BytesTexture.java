package net.zatrit.skins.lib.texture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.zatrit.skins.lib.api.Texture;
import net.zatrit.skins.lib.data.Metadata;

@Getter
@AllArgsConstructor
public class BytesTexture implements Texture {
    private final String id;
    private final byte[] bytes;
    private final Metadata metadata;
}
