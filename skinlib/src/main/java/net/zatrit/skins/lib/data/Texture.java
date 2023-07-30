package net.zatrit.skins.lib.data;

import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.zatrit.skins.lib.TextureType;

import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public class Texture {

    private final byte[] content;
    private final Map<String, String> metadata;
}
