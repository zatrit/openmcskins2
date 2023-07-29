package net.zatrit.skins.lib.resolver;

import lombok.AccessLevel;
import lombok.Getter;
import net.zatrit.skins.lib.data.Textures;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.URLPlayerLoader;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

import static net.andreinc.aleph.AlephFormatter.str;

public class LocalResolver implements Resolver {
    private final @Getter(AccessLevel.PROTECTED) Config config;
    private final String directory;

    public LocalResolver(
            Config config, String directory, Map<String, Object> replaces) {
        this.config = config;
        this.directory = str(directory).args(replaces).fmt();
    }

    @Override
    public boolean cacheable() {
        return false;
    }

    @Override
    public @NotNull Resolver.PlayerLoader resolve(Profile profile) {
        System.out.println(directory);

        return new URLPlayerLoader(
                getConfig().getCacheProvider(),
                new Textures(new EnumMap<>(TextureType.class)),
                this
        );
    }
}
