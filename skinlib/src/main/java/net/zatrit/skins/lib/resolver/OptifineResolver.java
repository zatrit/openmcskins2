package net.zatrit.skins.lib.resolver;

import lombok.*;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.URLPlayerLoader;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.data.Textures;
import net.zatrit.skins.lib.util.NetworkUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.HashMap;

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
        val textures = new Textures(new EnumMap<>(TextureType.class));
        val url = this.baseUrl + "/capes/" + profile.getName() + ".png";
        val metadata = new HashMap<String, String>();

        if (NetworkUtil.isOk(new URL(url))) {
            textures.getTextures().put(
                    TextureType.CAPE,
                    new Textures.TextureData(url, metadata)
            );
        }

        return new URLPlayerLoader(config.getCacheProvider(), textures, this);
    }
}
