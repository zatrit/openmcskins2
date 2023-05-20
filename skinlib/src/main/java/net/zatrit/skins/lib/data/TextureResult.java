package net.zatrit.skins.lib.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.zatrit.skins.lib.TextureType;

@Getter
@AllArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class TextureResult {
    private final Texture texture;
    private final TextureType type;
}
