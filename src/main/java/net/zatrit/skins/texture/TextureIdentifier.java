package net.zatrit.skins.texture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.Identifier;
import net.zatrit.skins.lib.TextureType;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class TextureIdentifier {
    private String name;
    private TextureType type;

    public Identifier asId() {
        return new Identifier("skins", name.hashCode() + "_" + type.ordinal());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
