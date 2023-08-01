package net.zatrit.skins.lib;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import net.zatrit.skins.lib.api.RawTexture;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.data.Textures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
public class BasePlayerLoader<T extends RawTexture> implements Resolver.PlayerLoader {
    private final @NotNull Textures<T> textures;

    @Override
    public boolean hasTexture(TextureType type) {
        return this.textures.getTextures().containsKey(type);
    }

    @Override
    public @Nullable RawTexture download(TextureType type) {
        if (!this.hasTexture(type)) {
            return null;
        }

        val texture = this.textures.getTextures().get(type);
        return this.wrapTexture(texture);
    }

    /**
     * Loads texture
     */
    protected RawTexture wrapTexture(@NotNull T texture) {
        return texture;
    }
}
