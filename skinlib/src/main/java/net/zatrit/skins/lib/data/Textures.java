package net.zatrit.skins.lib.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.Texture;

import java.util.EnumMap;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Textures<T extends Texture> {
    private EnumMap<TextureType, T> textures = new EnumMap<>(TextureType.class);
}