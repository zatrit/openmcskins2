package net.zatrit.skins.lib.data;

import lombok.Data;
import net.zatrit.skins.lib.TextureType;

import java.util.EnumMap;
import java.util.Map;

@SuppressWarnings("ClassCanBeRecord")
public @Data class Textures {
    private final EnumMap<TextureType, TextureData> textures;

    public static @Data class TextureData {
        private String url;
        private Map<String, String> metadata;
    }
}

