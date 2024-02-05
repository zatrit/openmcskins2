package net.zatrit.skins.lib.resolver.capes;

import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.resolver.CapesListResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class WurstResolver extends CapesListResolver {
    private static final String CAPES_URL = "https://www.wurstclient.net/api/v1/capes.json";

    public WurstResolver(Config config) {
        super(config);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, String> fetchList() throws IOException {
        return (Map<String, String>) this.config.getGson()
            .fromJson(
                new InputStreamReader(new URL(CAPES_URL).openStream()),
                TreeMap.class
            );
    }

    @Override
    protected String getUrl(String capeName) {
        return capeName;
    }

    @Override
    protected @Nullable String getCapeName(@NotNull Profile profile) {
        return Objects.requireNonNull(this.owners)
            .getOrDefault(
                profile.getName(),
                this.owners.get(profile.getId().toString())
            );
    }
}
