package net.zatrit.skins.lib.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class MCCapesResponse {
    private boolean animatedCape;
    private Map<String, String> textures;
}
