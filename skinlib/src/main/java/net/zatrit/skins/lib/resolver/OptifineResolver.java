package net.zatrit.skins.lib.resolver;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import lombok.*;
import net.zatrit.skins.lib.CachedPlayerLoader;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.data.Textures;
import net.zatrit.skins.lib.texture.BytesTexture;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.EnumMap;

@AllArgsConstructor
public class OptifineResolver implements Resolver {
    private final @Getter(AccessLevel.PROTECTED) Config config;
    private final @Getter String baseUrl;

    @Override
    public boolean requiresUuid() {
        return false;
    }

    @Override
    public @NotNull Resolver.PlayerLoader resolve(@NotNull Profile profile)
            throws IOException {
        val textures = new Textures<BytesTexture>(new EnumMap<>(TextureType.class));
        val url = new URL(this.baseUrl + "/capes/" + profile.getName() + ".png");
        val connection = (HttpURLConnection) url.openConnection();

        if (connection.getResponseCode() / 100 == 2) {
            @Cleanup val stream = connection.getInputStream();
            val content = ByteStreams.toByteArray(stream);
            val id = Hashing.murmur3_128().hashBytes(content).toString();
            val texture = new BytesTexture(id, content, Collections.emptyMap());

            textures.getTextures().put(TextureType.CAPE, texture);
        }

        return new CachedPlayerLoader<>(config.getCacheProvider(), textures);
    }
}
