package net.zatrit.skins.lib.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.RawTexture;

@Getter
@AllArgsConstructor
public class TextureResult {
    private final RawTexture texture;
    private final TextureType type;
}
