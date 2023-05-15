package net.zatrit.skins.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.zatrit.skins.SkinsClient;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static net.zatrit.skins.lib.util.SneakyLambda.sneaky;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {
    private static final List<Resolver> resolvers = Arrays.asList(
            new MojangResolver(
                    SkinsClient.getSkinsConfig()),
            new NamedHTTPResolver(
                    SkinsClient.getSkinsConfig(),
                    "http://skinsystem.ely.by/"
            )
    );
    @Shadow private boolean texturesLoaded;
    @Shadow @Final
    private Map<MinecraftProfileTexture.Type, Identifier> textures;
    @Shadow @Nullable private String model;

    @Shadow
    public abstract GameProfile getProfile();

    @Inject(method = "loadTextures", at = @At("HEAD"), cancellable = true)
    public void loadTextures(@NotNull CallbackInfo ci) {
        ci.cancel();

        if (!this.texturesLoaded) {
            this.texturesLoaded = true;

            final var profile = (Profile) getProfile();
            final var skinLoader = SkinsClient.getSkinLoader();

            CompletableFuture<Profile> profileTask;
            if (resolvers.stream().anyMatch(Resolver::requiresUuid)) {
                profileTask = profile.refreshUuidAsync()
                                      .exceptionallyAsync(error -> {
                                          // If UUID refresh failed
                                          error.printStackTrace();
                                          return profile;
                                      });
            } else {
                profileTask = CompletableFuture.completedFuture(profile);
            }

            profileTask.thenApplyAsync(profile1 -> skinLoader.fetchAsync(
                    resolvers,
                    profile1
            ).join()).whenComplete(sneaky((result, error) -> {
                if (error != null) {
                    error.printStackTrace();
                }

                for (final var textureResult : result) {
                    final var texture = textureResult.getTexture();
                    final var image = NativeImage.read(new ByteArrayInputStream(
                            texture.getContent()));
                    final var playerTexture = new NativeImageBackedTexture(image);
                    final var id = MinecraftClient.getInstance()
                                           .getTextureManager()
                                           .registerDynamicTexture(
                                                   "skins",
                                                   playerTexture
                                           );

                    final var type = TextureTypeUtil.toAuthlibType(textureResult.getType());
                    this.textures.put(type, id);

                    final var metadata = texture.getMetadata();

                    if (metadata == null) {
                        return;
                    }

                    if (texture.getMetadata().containsKey("model")) {
                        this.model = texture.getMetadata().get("model");
                    }
                }
            }));
        }
    }
}
