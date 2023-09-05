package net.zatrit.skins.lib.data;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.Texture;

import java.util.EnumMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Textures<T extends Texture> {
    private Map<TextureType, T> textures = Maps.newEnumMap(TextureType.class);
}