package net.zatrit.skins.lib.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.zatrit.skins.lib.TextureType;

import java.util.EnumMap;

@Getter
@AllArgsConstructor
public class MCCapesResponse {
    private boolean animatedCape;
    private EnumMap<TextureType, String> textures;
}
