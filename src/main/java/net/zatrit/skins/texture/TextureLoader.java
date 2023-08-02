package net.zatrit.skins.texture;

import net.minecraft.util.Identifier;
import net.zatrit.skins.lib.api.Texture;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Consumer;

public interface TextureLoader {
    static @NotNull TextureLoader create(Texture texture) {
        return TextureLoaderImpl.create(texture);
    }

    /**
     * @param callback - the code that will be executed when
     *                 the texture is registered.
     * @implNote the callback will be executed on the rendering thread.
     */
    void getTexture(
            @NotNull TextureIdentifier id,
            @NotNull Consumer<Identifier> callback) throws IOException;
}
