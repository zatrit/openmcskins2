package net.zatrit.skins.util.command;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.val;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

@AllArgsConstructor
public class DirectoryFileProvider implements FileProvider {
    private final Path path;

    @Override
    public Collection<String> listFiles() throws IOException {
        @Cleanup val files = Files.list(this.path);
        return files.map(p -> FilenameUtils.getName(p.toString())).toList();
    }

    @Override
    public @Nullable InputStream getFile(String name) throws IOException {
        val path = this.path.resolve(name);
        return Files.newInputStream(path);
    }
}
