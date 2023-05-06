package net.zatrit.skins.lib.data;

import java.util.Map;
import lombok.Data;

public @Data class Texture {
    final byte[] content;
    final Map<String, String> metadata;
}
