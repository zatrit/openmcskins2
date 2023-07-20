package net.zatrit.skins.lib.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import net.zatrit.skins.lib.TextureType;

import java.util.EnumMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class Textures {
    private final EnumMap<TextureType, TextureData> textures;

    @Getter
    @AllArgsConstructor
    public static class TextureData {
        private String url;
        private Map<String, String> metadata;
    }
}

