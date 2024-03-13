package net.zatrit.skins.lib.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.texture.URLTexture;
import org.jetbrains.annotations.ApiStatus;

import java.util.EnumMap;
import java.util.Map;

/**
 * Container for textures provided by Mojang-like APIs.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiStatus.Internal
public class MojangTextures {
    private Map<TextureType, URLTexture> textures = new EnumMap<>(TextureType.class);
}