package net.zatrit.skins.cache;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.AllArgsConstructor;
import lombok.val;
import net.zatrit.skins.SkinsClient;
import net.zatrit.skins.accessor.HasAssetPath;
import zatrit.skins.lib.api.Cache;

@AllArgsConstructor
public class AssetCache implements Cache {
  public static final String CACHE_ID = "omcs";

  private final HasAssetPath pathProvider;

  @Override
  public InputStream getCachedInputStream(String id, LoadFunction load) throws IOException {
    val path = this.pathProvider.getAssetPath().resolve(CACHE_ID);
    val function = SkinsClient.getHashFunction();
    val hash = function.hashUnencodedChars(id).toString();

    val cacheFile = Paths.get(path.toString(), hash.substring(0, 2), hash);

    if (cacheFile.toFile().exists()) {
      return Files.newInputStream(cacheFile);
    } else {
      val content = load.getInputStream();
      cacheFile.getParent().toFile().mkdirs();
      Files.copy(content, cacheFile);
      return content;
    }
  }
}
