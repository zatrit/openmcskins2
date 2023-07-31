package net.zatrit.skins.util.command;

import lombok.AllArgsConstructor;
import lombok.val;
import net.zatrit.skins.SkinsClient;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
public class DirectoryFileProvider implements FileProvider {
    private final Path path;

    @Override
    public Collection<String> listFiles() {
        val files = this.path.toFile().listFiles();

        if (files == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(files).map(File::getName).toList();
    }

    @Override
    public @Nullable InputStream getFile(String name) {
        val path = this.path.resolve(name);

        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            SkinsClient.getErrorHandler().accept(e);
            return null;
        }
    }
}
