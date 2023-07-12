package net.zatrit.skins.texture;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

public interface TextureLoader {
    Identifier getTexture(byte @NotNull [] content) throws IOException;

    static @NotNull TextureLoader fromMetadata(@Nullable Map<String, String> metadata) {
        return TextureLoaderImpl.fromMetadata(metadata);
    }
}
