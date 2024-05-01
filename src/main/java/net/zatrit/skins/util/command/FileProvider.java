package net.zatrit.skins.util.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

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

    /**
     * Opens an InputStream from an abstract file.
     */
    @Nullable Path getFile(String path);
}
