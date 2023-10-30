package net.zatrit.skins.lib.util;

import com.google.common.io.ByteStreams;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.NoArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@ApiStatus.Internal
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IOUtil {
    public static byte @Nullable [] download(@NotNull URL url)
            throws IOException {
        val connection = url.openConnection();

        // An easy way to check that the code means OK (2XX).
        if (connection instanceof HttpURLConnection) {
            val httpConnection = (HttpURLConnection) connection;
            if (httpConnection.getResponseCode() / 100 != 2) {
                return null;
            }
        }

        @Cleanup val stream = connection.getInputStream();
        return ByteStreams.toByteArray(stream);
    }
}
