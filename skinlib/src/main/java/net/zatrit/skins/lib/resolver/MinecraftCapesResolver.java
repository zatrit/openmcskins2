package net.zatrit.skins.lib.resolver;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.val;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.data.MCCapesResponse;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@AllArgsConstructor
public class MinecraftCapesResolver implements Resolver {
    private static final String BASE_URL = "https://api.minecraftcapes.net/profile/";
    private final Config config;

    @Override
    public @NotNull Resolver.PlayerLoader resolve(@NotNull Profile profile)
            throws IOException {
        val url = BASE_URL + profile.getId().toString().replace("-", "");
        @Cleanup
        val reader = new InputStreamReader(new URL(url).openStream());
        val response = this.config.getGson().fromJson(reader, MCCapesResponse.class);

        System.out.println(response);

        throw new IOException();
    }
}
