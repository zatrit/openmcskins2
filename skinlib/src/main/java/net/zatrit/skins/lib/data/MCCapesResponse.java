package net.zatrit.skins.lib.data;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class MCCapesResponse {
    private boolean animatedCapes;
    private Map<String, String> textures;
}
