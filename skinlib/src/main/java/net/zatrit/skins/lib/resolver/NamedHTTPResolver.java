package net.zatrit.skins.lib.resolver;

import com.google.gson.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import net.zatrit.skins.lib.Profile;
import net.zatrit.skins.lib.Skins;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.TexturesPlayerHandler;
import net.zatrit.skins.lib.data.Textures;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.EnumMap;

@ToString
@AllArgsConstructor
public class NamedHTTPResolver implements Resolver {
    private final transient @Getter(AccessLevel.PROTECTED) Skins skins;
    private final @Getter String baseUrl;

    @Override
    public boolean requiresUuid() {
        return false;
    }

    @Override
    public @NotNull PlayerHandler resolve(@NotNull Profile profile) throws IOException {
        final var url = new URL(this.getBaseUrl() + "/textures/" + profile.getName());

        final var type = new TypeToken<EnumMap<TextureType, Textures.TextureData>>() {}.getType();
        final var textures = new Textures(
                getSkins().getGson().fromJson(new InputStreamReader(url.openStream()), type));

        return new TexturesPlayerHandler(getSkins(), textures, this);
    }
}
