package net.zatrit.skins.lib.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.zatrit.skins.lib.TextureType;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

/**
 * An API response from the Minecraft Capes
 * server containing player-related fields.
 */
@Getter
@AllArgsConstructor
@ApiStatus.Internal
public class MCCapesResponse {
    private boolean animatedCape;
    private Map<TextureType, String> textures;
}
