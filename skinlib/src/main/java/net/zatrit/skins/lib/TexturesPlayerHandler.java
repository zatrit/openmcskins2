package net.zatrit.skins.lib;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.zatrit.skins.lib.data.Texture;
import net.zatrit.skins.lib.data.Textures;
import net.zatrit.skins.lib.resolver.Resolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

@AllArgsConstructor
public class TexturesPlayerHandler implements Resolver.PlayerHandler {
    private final @Getter(AccessLevel.PROTECTED) Skins skins;
    private final @NotNull @Getter Textures textures;
    private final @NotNull @Getter Resolver resolver;


    @Override
    public boolean hasTexture(TextureType type) {
        return this.textures.getTextures().containsKey(type);
    }

    @Override
    public @Nullable Texture download(TextureType type) throws IOException {
        if (!this.hasTexture(type)) {
            return null;
        }

        final var textureData = this.textures.getTextures().get(type);

        return getSkins().fetchTextureData(textureData, this.resolver.cacheable());
    }
}
