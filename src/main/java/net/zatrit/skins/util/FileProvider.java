package net.zatrit.skins.util;

import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.Collection;

public interface FileProvider {
    Collection<String> listFiles();

    @Nullable InputStream getFile(String path);
}
