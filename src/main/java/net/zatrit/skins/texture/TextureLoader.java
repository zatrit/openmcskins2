package net.zatrit.skins.texture;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

public interface TextureLoader {
    static @NotNull TextureLoader create(
            TextureIdentifier id, @Nullable Map<String, String> metadata) {
        return TextureLoaderImpl.create(id, metadata);
    }

    /**
     * @param callback - the code that will be executed when
     *                 the texture is registered.
     * @implNote the callback will be executed on the rendering thread.
     */
    void getTexture(byte @NotNull [] content, Consumer<Identifier> callback)
            throws IOException;
}
