package net.zatrit.skins.lib.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NetworkUtil {
    public static boolean isOk(@NotNull URL url) throws IOException {
        val connection = (HttpURLConnection) url.openConnection();
        return connection.getResponseCode() / 100 == 2;
    }
}
