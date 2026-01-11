package zatrit.skins.accessor;

import java.nio.file.Path;

public interface HasAssetPath {
    /**
     * Used to cache skins.
     *
     * @return the path for the assets folder of the game.
     * @see zatrit.skins.mixin.MinecraftClientMixin
     * @see zatrit.skins.cache.AssetCache
     */
    Path getAssetPath();
}
