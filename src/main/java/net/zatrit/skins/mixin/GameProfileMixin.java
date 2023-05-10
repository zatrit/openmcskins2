package net.zatrit.skins.mixin;

import com.mojang.authlib.GameProfile;
import lombok.SneakyThrows;
import net.zatrit.skins.SkinsClient;
import net.zatrit.skins.lib.api.Profile;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

@Mixin(value = GameProfile.class, remap = false)
public abstract class GameProfileMixin implements Profile {
    @Override
    public abstract @Shadow UUID getId();

    @Final
    @Mutable
    public abstract @Accessor void setId(UUID id);

    @Override
    public abstract @Shadow String getName();

    @Override
    @SneakyThrows
    public void refreshUuid() {
        final var url = new URL("https://api.mojang.com/users/profiles/minecraft/" +
                                        this.getName());

        final var map = SkinsClient.getSkinsConfig()
                                   .getGson()
                                   .fromJson(
                                           new InputStreamReader(url.openStream()),
                                           Map.class
                                   );

        if (map != null) {
            final var id = UUID.fromString(String.valueOf(map.get("id"))
                                                 .replaceAll(
                                                         "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                                                         "$1-$2-$3-$4-$5"
                                                 ));

            this.setId(id);
        }
    }
}

