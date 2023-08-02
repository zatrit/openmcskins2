package net.zatrit.skins.lib.texture;

import com.google.common.io.ByteStreams;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Getter;
import lombok.val;
import net.zatrit.skins.lib.api.Texture;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

@AllArgsConstructor
public class URLTexture implements Texture {
    private String url;
    private @Getter Map<String, String> metadata;

    @Override
    public String getId() {
        return this.url;
    }

    @Override
    public byte[] getBytes() throws IOException {
        @Cleanup val stream = new URL(this.url).openStream();
        return ByteStreams.toByteArray(stream);
    }
}