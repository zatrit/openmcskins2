package net.zatrit.skins.texture;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.util.Identifier;
import net.zatrit.skins.lib.TextureType;

@EqualsAndHashCode
@AllArgsConstructor
public class TextureIdentifier {
    private @Getter String name;
    private @Getter TextureType type;

    public Identifier asId() {
        return new Identifier("skins", name.hashCode() + "_" + type.ordinal());
    }
}
