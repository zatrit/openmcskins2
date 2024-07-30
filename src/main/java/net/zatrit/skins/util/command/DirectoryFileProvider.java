package net.zatrit.skins.util.command;

import lombok.AllArgsConstructor;
import lombok.val;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

@AllArgsConstructor
public class DirectoryFileProvider implements FileProvider {
    private final Path path;

    @SuppressWarnings("resource")
    @Override
    public Collection<String> listFiles() throws IOException {
        return Files.list(this.path)
            .map(p -> FilenameUtils.getName(p.toString())).toList();
    }

    @Override
    public @Nullable Path getFile(String name) {
        val path = this.path.resolve(name);
        return Files.isRegularFile(path) ? path : null;
    }
}
