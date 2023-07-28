package net.zatrit.skins.cache;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import net.zatrit.skins.accessor.HasAssetPath;
import net.zatrit.skins.SkinsClient;
import net.zatrit.skins.lib.api.cache.Cache;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@AllArgsConstructor
public class AssetCache implements Cache {
    private final HasAssetPath pathProvider;
    private final String type;

    /**
     * {@inheritDoc}
     */
    @Override
    @SneakyThrows
    public byte[] getOrLoad(String id, LoadFunction load) {
        val path = Paths.get(this.pathProvider.getAssetPath(), type);
        val function = SkinsClient.getHashFunction();
        val hash = function.hashUnencodedChars(id).toString();

        val cacheFile = Paths.get(
                path.toString(),
                hash.substring(0, 2),
                hash
        );

        if (cacheFile.toFile().exists()) {
            return Files.readAllBytes(cacheFile);
        } else {
            val content = load.load();
            // noinspection ResultOfMethodCallIgnored
            cacheFile.getParent().toFile().mkdirs();
            Files.write(cacheFile, content, StandardOpenOption.CREATE);
            return content;
        }
    }
}
