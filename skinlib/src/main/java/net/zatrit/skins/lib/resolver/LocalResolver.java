package net.zatrit.skins.lib.resolver;

import com.google.gson.reflect.TypeToken;
import lombok.Cleanup;
import lombok.val;
import net.zatrit.skins.lib.BasePlayerLoader;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.api.Texture;
import net.zatrit.skins.lib.data.Textures;
import net.zatrit.skins.lib.texture.URLTexture;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static net.andreinc.aleph.AlephFormatter.str;

public class LocalResolver implements Resolver {
    private final Config config;
    private final Path directory;

    public LocalResolver(
            Config config, String directory, Map<String, Object> replaces) {
        this.config = config;
        this.directory = Paths.get(str(directory).args(replaces).fmt());
    }

    @Override
    public @NotNull Resolver.PlayerLoader resolve(@NotNull Profile profile)
            throws IOException {
        val name = profile.getName();
        val textures = new EnumMap<TextureType, Texture>(TextureType.class);

        val metadataDir = this.directory.resolve("metadata");
        val texturesDir = this.directory.resolve("textures");

        for (val type : TextureType.values()) {
            val metadata = new HashMap<String, String>();
            val typeName = type.toString().toLowerCase();
            val texturesFile = texturesDir.resolve(typeName)
                                       .resolve(name + ".png").toFile();

            if (!texturesFile.isFile()) {
                continue;
            }

            val url = texturesFile.toURI().toURL().toString();
            val metadataFile = metadataDir.resolve(typeName)
                                       .resolve(name + ".json");

            if (metadataFile.toFile().isFile()) {
                @Cleanup val reader = Files.newBufferedReader(metadataFile);

                val metadataType = TypeToken.getParameterized(HashMap.class,
                        String.class,
                        String.class
                ).getType();

                metadata.putAll(this.config.getGson()
                                        .fromJson(reader, metadataType));
            }

            textures.put(type, new URLTexture(url, metadata));
        }

        return new BasePlayerLoader<>(new Textures<>(textures));
    }
}
