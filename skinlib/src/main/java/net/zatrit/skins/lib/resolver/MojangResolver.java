package net.zatrit.skins.lib.resolver;

import lombok.*;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.URLPlayerLoader;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.data.MojangResponse;
import net.zatrit.skins.lib.data.Textures;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Base64;

/**
 * <a href="https://wiki.vg/Mojang_API">Mojang API</a> implementation
 * for OpenMCSkins.
 */
@AllArgsConstructor
public final class MojangResolver implements Resolver {
    private final @Getter(AccessLevel.PROTECTED) Config config;

    @Override
    @Contract("_ -> new")
    public @NotNull Resolver.PlayerLoader resolve(@NotNull Profile profile)
            throws IOException {
        val gson = getConfig().getGson();
        val url = "https://sessionserver.mojang.com/session/minecraft/profile/" +
                          profile.getId().toString().replaceAll("-", "");

        MojangResponse response;
        @Cleanup val stream = new URL(url).openStream();

        response = gson.fromJson(new InputStreamReader(stream),
                MojangResponse.class
        );

        val decoder = Base64.getDecoder();
        val textureData = decoder.decode(response.getProperties().get(0)
                                                 .getValue());
        @Cleanup
        val bytesReader = new InputStreamReader(new ByteArrayInputStream(
                textureData));

        val textures = gson.fromJson(bytesReader, Textures.class);

        return new URLPlayerLoader(getConfig().getCacheProvider(),
                textures,
                this
        );
    }
}
