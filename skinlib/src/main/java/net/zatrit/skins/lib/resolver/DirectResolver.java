package net.zatrit.skins.lib.resolver;

import lombok.AllArgsConstructor;
import lombok.val;
import net.andreinc.aleph.AlephFormatter;
import net.zatrit.skins.lib.BasePlayerLoader;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.PlayerLoader;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.data.Metadata;
import net.zatrit.skins.lib.data.Textures;
import net.zatrit.skins.lib.texture.BytesTexture;
import net.zatrit.skins.lib.util.IOUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;

@AllArgsConstructor
public class DirectResolver implements Resolver {
    private final Config config;
    private final String baseUrl;
    private final Collection<TextureType> types;

    @Override
    public boolean requiresUuid() {
        return this.baseUrl.contains("{id}") || this.baseUrl.contains("shortId");
    }

    @Override
    public @NotNull PlayerLoader resolve(@NotNull Profile profile)
            throws IOException {
        val textures = new Textures<BytesTexture>();
        val replaces = new HashMap<String, Object>();
        replaces.put("id", profile.getId());
        replaces.put("name", profile.getName());
        replaces.put("shortId", profile.getId().toString().replace("-", ""));

        for (val type : this.types) {
            replaces.put("type", type);

            val url = new URL(AlephFormatter.str(this.baseUrl, replaces).fmt());
            val texture = new BytesTexture(
                    url.toString(),
                    IOUtil.download(url),
                    new Metadata()
            );

            textures.getTextures().put(type, texture);
        }

        /* Since you can't check for the existence/change of a
        texture without fetching that texture, it should not be cached. */
        return new BasePlayerLoader<>(textures, this.config.getLayers());
    }
}
