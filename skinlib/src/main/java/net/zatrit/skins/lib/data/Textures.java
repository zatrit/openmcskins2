package net.zatrit.skins.lib.data;

import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;
import lombok.*;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.Texture;

import java.util.Map;

/**
 * Container for player textures
 *
 * @param <T> texture type.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Textures<T extends Texture> {
    @SerializedName("textures")
    private Map<TextureType, T> map = Maps.newEnumMap(TextureType.class);
}