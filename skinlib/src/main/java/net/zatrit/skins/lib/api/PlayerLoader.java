package net.zatrit.skins.lib.api;

import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.data.TypedTexture;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Player-specific textures loader.
 */
public interface PlayerLoader {
    /**
     * @return true, if texture is present.
     */
    boolean hasTexture(TextureType type);

    /**
     * @return texture of specified type if
     * present (check via {@link #hasTexture}).
     */
    @Nullable TypedTexture getTexture(TextureType type) throws IOException;
}
