package net.zatrit.skins.lib.resolver;

import net.zatrit.skins.lib.Profile;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.data.Texture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public interface Resolver {
    default boolean requiresUuid() {
        return true;
    }

    default boolean cacheable() {
        return true;
    }

    @NotNull PlayerHandler resolve(Profile profile) throws IOException;

    interface PlayerHandler {
        boolean hasTexture(TextureType type);

        @Nullable Texture download(TextureType type) throws IOException;
    }
}
