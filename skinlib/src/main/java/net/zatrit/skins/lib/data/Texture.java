package net.zatrit.skins.lib.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class Texture {
    private final byte[] content;
    private final Map<String, String> metadata;
}
