package net.zatrit.skins;

public interface AssetPathProvider {
    /**
     * Used to cache skins.
     *
     * @return the path for the assets folder of the game.
     * @see net.zatrit.skins.mixin.MinecraftClientMixin
     * @see net.zatrit.skins.cache.AssetCache
     */
    String getAssetPath();
}
