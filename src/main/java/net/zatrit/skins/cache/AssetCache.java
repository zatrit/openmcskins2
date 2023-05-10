package net.zatrit.skins.cache;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.zatrit.skins.AssetPathProvider;
import net.zatrit.skins.SkinsClient;
import net.zatrit.skins.lib.api.cache.Cache;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@AllArgsConstructor
public class AssetCache implements Cache {
    private final AssetPathProvider pathProvider;
    private final String type;

    @Override
    @SneakyThrows
    public byte[] get(String id, LoadFunction load) {
        final var path = Paths.get(this.pathProvider.getAssetPath(), type);

        final var function = SkinsClient.getSkinsConfig()
                                        .getHashFunc()
                                        .getFunction();

        final var hash = function.hashUnencodedChars(id).toString();

        final var cacheFile = Paths.get(
                path.toString(),
                hash.substring(0, 2),
                hash
        );

        if (cacheFile.toFile().exists()) {
            return Files.readAllBytes(cacheFile);
        } else {
            final var content = load.load();
            // noinspection ResultOfMethodCallIgnored
            cacheFile.getParent().toFile().mkdirs();
            Files.write(cacheFile, content, StandardOpenOption.CREATE);
            return content;
        }
    }
}
