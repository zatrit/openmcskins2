package net.zatrit.skins.lib.data;

import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.texture.URLTexture;
import org.jetbrains.annotations.ApiStatus;

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
    @SerializedName("textures")
    private Map<TextureType, URLTexture> map = Maps.newEnumMap(TextureType.class);
}