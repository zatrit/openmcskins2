package net.zatrit.skins.lib.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class Texture {
    final byte[] content;
    final Map<String, String> metadata;
}
