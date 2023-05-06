package net.zatrit.skins.lib.data;

import java.util.EnumMap;
import java.util.Map;
import lombok.Data;
import net.zatrit.skins.lib.TextureType;

public @Data class Textures {
    private final EnumMap<TextureType, TextureData> textures;

    public static @Data class TextureData {
        private String url;
        private Map<String, String> metadata;
    }
}

