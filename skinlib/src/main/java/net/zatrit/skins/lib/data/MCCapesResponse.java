package net.zatrit.skins.lib.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.zatrit.skins.lib.TextureType;

import java.util.Map;

@Getter
@AllArgsConstructor
public class MCCapesResponse {
    private boolean animatedCape;
    private Map<TextureType, String> textures;
}
