package net.zatrit.skins.lib.data;

import lombok.Data;

import java.util.Map;

public @Data class Texture {
    final byte[] content;
    final Map<String, String> metadata;
}

