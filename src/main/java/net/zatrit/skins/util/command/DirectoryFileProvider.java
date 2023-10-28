package net.zatrit.skins.util.command;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.val;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public class DirectoryFileProvider implements FileProvider {
    private final Path path;

    @Override
    public Collection<String> listFiles() throws IOException {
        @Cleanup val files = Files.list(this.path);
        return files.map(p -> FilenameUtils.getName(p.toString())).collect(
                Collectors.toList());
    }

    @Override
    public @NotNull Optional<Path> getFile(String name) {
        val path = this.path.resolve(name);
        return Files.isRegularFile(path) ? Optional.of(path) : Optional.empty();
    }
}
