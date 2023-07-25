package net.zatrit.skins.texture;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

public interface TextureLoader {
    static @NotNull TextureLoader create(
            TextureIdentifier id,
            @Nullable Map<String, String> metadata) {
        return TextureLoaderImpl.create(id, metadata);
    }

    Identifier getTexture(byte @NotNull [] content) throws IOException;
}
