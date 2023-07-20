package net.zatrit.skins.util.command;

import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.Collection;

/**
 * An interface for reading and listing abstract files.
 *
 * @see IndexedResourceProvider
 * @see DirectoryFileProvider
 */
public interface FileProvider {
    /**
     * @return a collection of file names.
     */
    Collection<String> listFiles();

    /**
     * Opens an InputStream from an abstract file.
     */
    @Nullable InputStream getFile(String path);
}
