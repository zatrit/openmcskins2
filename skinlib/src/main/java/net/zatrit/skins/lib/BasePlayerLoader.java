package net.zatrit.skins.lib;

import lombok.AllArgsConstructor;
import lombok.val;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.api.Texture;
import net.zatrit.skins.lib.data.TextureResult;
import net.zatrit.skins.lib.data.Textures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Basic implementation of the player loader. Implies that the texture will
 * not be cached and should be used in cases where getting a player fully loads
 * the texture. In other cases {@link CachedPlayerLoader} should be used.
 *
 * @see Resolver.PlayerLoader
 * @see CachedPlayerLoader
 */
@AllArgsConstructor
public class BasePlayerLoader<T extends Texture> implements Resolver.PlayerLoader {
    private final @NotNull Textures<T> textures;

    @Override
    public boolean hasTexture(TextureType type) {
        return this.textures.getTextures().containsKey(type);
    }

    @Override
    public @Nullable TextureResult getTexture(TextureType type) {
        if (!this.hasTexture(type)) {
            return null;
        }

        val texture = this.wrapTexture(this.textures.getTextures().get(type));
        return new TextureResult(texture, type);
    }

    protected Texture wrapTexture(@NotNull T texture) {
        return texture;
    }
}
