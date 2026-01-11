package zatrit.skins.util.command;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

/**
 * An interface for reading and listing abstract files.
 *
 * @see IndexedResourceProvider
 * @see DirectoryFileProvider
 * @see FileArgumentType
 */
public interface FileProvider {
  /**
   * @return a collection of file names.
   */
  Collection<String> listFiles() throws IOException;

  /** Opens an InputStream from an abstract file. */
  @NotNull
  Optional<Path> getFile(String path);
}
