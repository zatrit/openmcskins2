package net.zatrit.skins.util.command;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static net.zatrit.skins.lib.util.SneakyLambda.sneaky;

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
    public @NotNull Optional<Path> getFile(String name) {
        val url = classLoader.getResource(this.path + "/" + name);
        return Optional.ofNullable(url).map(sneaky(u -> Path.of(u.toURI())));
    }
}
