package net.zatrit.skins.lib;

import lombok.AllArgsConstructor;
import lombok.val;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.api.Texture;
import net.zatrit.skins.lib.data.Textures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public class BasePlayerLoader<T extends Texture> implements Resolver.PlayerLoader {
    private final @NotNull Textures<T> textures;

    @Override
    public boolean hasTexture(TextureType type) {
        return this.textures.getTextures().containsKey(type);
    }

    @Override
    public @Nullable Texture getTexture(TextureType type) {
        if (!this.hasTexture(type)) {
            return null;
        }

        val texture = this.textures.getTextures().get(type);
        return this.wrapTexture(texture);
    }

    protected Texture wrapTexture(@NotNull T texture) {
        return texture;
    }
}
