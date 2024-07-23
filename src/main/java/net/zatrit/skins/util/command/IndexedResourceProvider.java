package net.zatrit.skins.util.command;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * A FileProvider implementation that reads a specific resource
 * file with the .index extension and uses it to get a list of
 * files in a specific resource folder (e.g. presets).
 *
 * @see FileProvider
 */
@AllArgsConstructor
public class IndexedResourceProvider implements FileProvider {
    private final String path;
    private final ClassLoader classLoader;

    @Override
    public Collection<String> listFiles() throws IOException {
        val url = classLoader.getResource(path + ".index");

        if (url != null) {
            @Cleanup val indexFile = url.openStream();
            return IOUtils.readLines(indexFile, Charset.defaultCharset());
        }

        return Collections.emptyList();
    }

    @Override
    @SneakyThrows
    public @Nullable Path getFile(String name) {
        val url = classLoader.getResource(this.path + "/" + name);

        if (url != null) {
            return Paths.get(url.toURI());
        } else {
            return null;
        }
    }
}
