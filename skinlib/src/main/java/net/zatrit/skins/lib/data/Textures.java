package net.zatrit.skins.lib.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.Texture;

import java.util.EnumMap;

@Getter
@AllArgsConstructor
public class Textures<T extends Texture> {
    private final EnumMap<TextureType, T> textures;

}

