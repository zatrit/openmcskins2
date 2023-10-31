package net.zatrit.skins;

import com.google.common.base.Verify;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import lombok.AllArgsConstructor;
import lombok.val;
import net.zatrit.skins.accessor.HasMetadata;
import net.zatrit.skins.lib.CachedPlayerTextures;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.PlayerTextures;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.data.Metadata;
import net.zatrit.skins.lib.texture.URLTexture;
import net.zatrit.skins.util.TextureTypeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

@AllArgsConstructor
public class FallbackResolver implements Resolver {
    private final Config config;
    private final MinecraftSessionService sessionService;

    @Override
    public @NotNull PlayerTextures resolve(Profile profile) {
        Verify.verify(profile instanceof GameProfile);

        val gameProfile = (GameProfile) profile;
        val textures = this.sessionService.getTextures(gameProfile, false);
        val newTextures = new EnumMap<TextureType, URLTexture>(TextureType.class);

        for (val entry : textures.entrySet()) {
            val texture = entry.getValue();
            @Nullable val metadataMap = ((HasMetadata) texture).getMetadata();

            val metadata = metadataMap == null ? new Metadata() : new Metadata(
                    Boolean.parseBoolean(metadataMap.getOrDefault(
                            "animated",
                            "false"
                    )),
                    metadataMap.getOrDefault("model", null)
            );

            newTextures.put(
                    TextureTypeUtil.fromAuthlibType(entry.getKey()),
                    new URLTexture(texture.getUrl(), metadata)
            );
        }

        return new CachedPlayerTextures<>(
                newTextures,
                config.getLayers(),
                config.getCacheProvider()
        );
    }
}
