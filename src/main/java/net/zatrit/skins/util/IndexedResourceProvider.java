package net.zatrit.skins.util;

import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
public class IndexedResourceProvider implements FileProvider {
    private final String path;
    private final ClassLoader classLoader;

    @Override
    public Collection<String> listFiles() {
        final var url = classLoader.getResource(path + ".index");

        if (url != null) {
            try (final var indexFile = url.openStream()) {
                return IOUtils.readLines(indexFile, Charset.defaultCharset());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Collections.emptyList();
    }

    @Override
    public @Nullable InputStream getFile(String name) {
        final var url = classLoader.getResource(this.path + "/" + name);

        if (url != null) {
            try {
                return url.openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
