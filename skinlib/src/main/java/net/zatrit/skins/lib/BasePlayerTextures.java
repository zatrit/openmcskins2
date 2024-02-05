package net.zatrit.skins.lib;

import lombok.AllArgsConstructor;
import lombok.val;
import net.zatrit.skins.lib.api.Layer;
import net.zatrit.skins.lib.api.PlayerTextures;
import net.zatrit.skins.lib.api.Texture;
import net.zatrit.skins.lib.data.TypedTexture;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * Basic implementation of the player textures. Implies that the texture will
 * not be cached and should be used in cases where getting a player fully loads
 * the texture. In other cases {@link CachedPlayerTextures} should be used.
 *
 * @see PlayerTextures
 * @see CachedPlayerTextures
 */
@AllArgsConstructor
public class BasePlayerTextures<T extends Texture> implements PlayerTextures {
    private final @NotNull Map<TextureType, T> map;
    private final @NotNull Collection<Layer<TypedTexture>> layers;

    @Override
    public boolean hasTexture(TextureType type) {
        return this.map.containsKey(type);
    }

    @Override
    public @Nullable TypedTexture getTexture(TextureType type) {
        if (!this.hasTexture(type)) {
            return null;
        }

        val texture = this.wrapTexture(this.map.get(type));

        // https://stackoverflow.com/a/44521687/12245612
        val layers = this.layers.stream().reduce(Layer::andThen).orElseThrow(
            NullPointerException::new);

        return layers.apply(new TypedTexture(texture, type));
    }

    @Contract(pure = true)
    protected Texture wrapTexture(@NotNull T texture) {
        return texture;
    }
}
