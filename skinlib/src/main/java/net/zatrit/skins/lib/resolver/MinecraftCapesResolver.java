package net.zatrit.skins.lib.resolver;

import lombok.*;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@AllArgsConstructor
public class MinecraftCapesResolver implements Resolver {
    private static final String BASE_URL = "https://api.minecraftcapes.net/profile/";
    private final @Getter(AccessLevel.PROTECTED) Config config;

    @Override
    public @NotNull Resolver.PlayerLoader resolve(@NotNull Profile profile)
            throws IOException {
        val url = BASE_URL + profile.getId().toString().replace("-", "");
        @Cleanup val reader = new InputStreamReader(new URL(url).openStream());

        throw new IOException();
    }
}
