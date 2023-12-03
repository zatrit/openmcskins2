package net.zatrit.skins.texture;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.util.Identifier;
import net.zatrit.skins.lib.TextureType;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class TextureIdentifier {
    private String name;
    private TextureType type;

    public Identifier asId() {
        return new Identifier("skins", name.hashCode() + "_" + type.ordinal());
    }
}
