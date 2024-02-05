package net.zatrit.skins.lib.resolver;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.val;
import net.zatrit.skins.lib.BasePlayerTextures;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.PlayerTextures;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.api.Texture;
import net.zatrit.skins.lib.data.Metadata;
import net.zatrit.skins.lib.texture.URLTexture;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;

/**
 * Resolver for the local directory, which has a similar format to the skin
 * server, i.e. loads by name from the {@code textures/} and
 * {@code metadata/} folders.
 */
@AllArgsConstructor
public final class LocalResolver implements Resolver {
    private final Config config;
    private final Path directory;

    @Override
    public @NotNull PlayerTextures resolve(@NotNull Profile profile)
        throws IOException {
        val name = profile.getName();
        val textures = new EnumMap<TextureType, Texture>(TextureType.class);

        val metadataDir = this.directory.resolve("metadata");
        val texturesDir = this.directory.resolve("textures");

        for (val type : TextureType.values()) {
            Metadata metadata = null;
            val typeName = type.toString().toLowerCase();
            val texturesFile = texturesDir.resolve(typeName).resolve(
                name + ".png").toFile();

            if (!texturesFile.isFile()) {
                continue;
            }

            val url = texturesFile.toURI().toURL().toString();
            val metadataFile = metadataDir.resolve(typeName).resolve(
                name + ".json");

            if (metadataFile.toFile().isFile()) {
                @Cleanup val reader = Files.newBufferedReader(metadataFile);

                metadata = this.config.getGson().fromJson(
                    reader,
                    Metadata.class
                );
            }

            textures.put(type, new URLTexture(url, metadata));
        }

        return new BasePlayerTextures<>(
            textures,
            this.config.getLayers()
        );
    }
}
