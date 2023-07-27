package net.zatrit.skins.lib.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class Texture {
    private final byte[] content;
    private final Map<String, String> metadata;
}
