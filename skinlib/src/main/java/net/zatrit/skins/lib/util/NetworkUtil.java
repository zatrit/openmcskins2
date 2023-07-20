package net.zatrit.skins.lib.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NetworkUtil {
    public static boolean hasContent(@NotNull URL url) {
        try {
            final var connection = (HttpURLConnection) url.openConnection();
            return connection.getContentLength() != 0;
        } catch (IOException e) {
            return false;
        }
    }
}
