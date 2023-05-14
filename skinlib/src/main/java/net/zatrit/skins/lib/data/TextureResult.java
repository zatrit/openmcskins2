package net.zatrit.skins.lib.data;

import lombok.Data;
import net.zatrit.skins.lib.TextureType;

@SuppressWarnings("ClassCanBeRecord")
public @Data class TextureResult {
    final Texture texture;
    final TextureType type;
}
