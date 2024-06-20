package net.zatrit.skins;

import com.google.common.base.Verify;
import com.google.common.base.VerifyException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import lombok.AllArgsConstructor;
import lombok.val;
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

import java.util.EnumMap;
import java.util.Optional;

@AllArgsConstructor
public class FallbackResolver implements Resolver {
    private final Config config;
    private final MinecraftSessionService sessionService;

    @Override
    public @NotNull PlayerTextures resolve(Profile profile)
        throws VerifyException {
        Verify.verify(profile instanceof GameProfile);

        val gameProfile = (GameProfile) profile;
        val textures = this.sessionService.getTextures(gameProfile);
        val newTextures = new EnumMap<TextureType, URLTexture>(TextureType.class);

        for (val type : MinecraftProfileTexture.Type.values()) {
            val texture = switch (type) {
                case SKIN -> textures.skin();
                case CAPE -> textures.cape();
                default -> null;
            };

            if (texture == null) {
                continue;
            }

            val metadata = new Metadata(
                Boolean.parseBoolean(texture.getMetadata("animated")),
                texture.getMetadata("model")
            );

            newTextures.put(
                TextureTypeUtil.fromAuthlibType(type),
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