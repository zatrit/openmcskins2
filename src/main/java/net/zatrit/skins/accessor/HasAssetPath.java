package net.zatrit.skins.accessor;

import java.nio.file.Path;

public interface HasAssetPath {
    /**
     * Used to cache skins.
     *
     * @return the path for the assets folder of the game.
     * @see net.zatrit.skins.mixin.MinecraftClientMixin
     * @see net.zatrit.skins.cache.AssetCache
     */
    Path getAssetPath();
}
