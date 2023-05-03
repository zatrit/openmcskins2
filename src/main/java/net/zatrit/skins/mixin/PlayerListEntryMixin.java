package net.zatrit.skins.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import lombok.SneakyThrows;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.zatrit.skins.SkinsClient;
import net.zatrit.skins.lib.Profile;
import net.zatrit.skins.lib.resolver.MojangResolver;
import net.zatrit.skins.lib.resolver.NamedHTTPResolver;
import net.zatrit.skins.util.TextureTypeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Map;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {
    @Shadow private boolean texturesLoaded;

    @Shadow
    public abstract GameProfile getProfile();

    @Shadow @Final private Map<MinecraftProfileTexture.Type, Identifier> textures;

    @Shadow @Nullable private String model;

    @SneakyThrows
    @Inject(method = "loadTextures", at = @At("HEAD"), cancellable = true)
    public void loadTextures(@NotNull CallbackInfo ci) {
        ci.cancel();

        if (!this.texturesLoaded) {
            this.texturesLoaded = true;

            final var skins = SkinsClient.getSkins();
            final var resolvers = Arrays.asList(new MojangResolver(skins),
                    new NamedHTTPResolver(skins, "http://skinsystem.ely.by/"));

            final var profile = (Profile) getProfile();
            profile.refreshUuid();

            skins.getSkinLoader().fetchAsync(resolvers, profile, textureResult -> {
                final var texture = textureResult.getTexture();
                try {
                    final var image = NativeImage.read(new ByteArrayInputStream(
                            texture.getContent()));
                    final var playerTexture = new NativeImageBackedTexture(image);
                    final var id = MinecraftClient.getInstance()
                            .getTextureManager()
                            .registerDynamicTexture("skins", playerTexture);

                    final var type = TextureTypeUtil.toAuthlibType(textureResult.getType());
                    this.textures.put(type, id);

                    final var metadata = texture.getMetadata();

                    if (metadata == null) {
                        return;
                    }

                    if (texture.getMetadata().containsKey("model")) {
                        this.model = texture.getMetadata().get("model");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }, Throwable::printStackTrace);
        }
    }
}
