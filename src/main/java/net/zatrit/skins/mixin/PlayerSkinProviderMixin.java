package net.zatrit.skins.mixin;

import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import lombok.SneakyThrows;
import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.zatrit.skins.SkinsClient;
import net.zatrit.skins.accessor.Refreshable;
import net.zatrit.skins.lib.api.PlayerLoader;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.data.TypedTexture;
import net.zatrit.skins.lib.util.Enumerated;
import net.zatrit.skins.texture.TextureIdentifier;
import net.zatrit.skins.texture.TextureLoader;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Mixin(PlayerSkinProvider.class)
public class PlayerSkinProviderMixin implements Refreshable {
    @Shadow @Final
    public LoadingCache<PlayerSkinProvider.Key, CompletableFuture<SkinTextures>> cache;

    @Inject(
            at = @At("HEAD"),
            method = "fetchSkinTextures(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/texture/PlayerSkinProvider$Textures;)Ljava/util/concurrent/CompletableFuture;",
            cancellable = true)
    public void fetchSkinTextures(
            GameProfile profile2,
            PlayerSkinProvider.Textures uselessTextures,
            CallbackInfoReturnable<CompletableFuture<SkinTextures>> cir) {
        val profile = (Profile) profile2;
        val dispatcher = SkinsClient.getDispatcher();
        val resolvers = SkinsClient.getResolvers();

        val config = SkinsClient.getConfigHandler().instance();
        val timeout = (int) (config.getLoaderTimeout() * 1000);
        val refreshUuid = switch (config.getUuidMode()) {
            case NEVER -> false;
            case ALWAYS -> true;
            case OFFLINE -> {
                val client = MinecraftClient.getInstance();
                val networkHandler = client.getNetworkHandler();

                yield networkHandler != null &&
                              !networkHandler.getConnection().isEncrypted();
            }
        };

        CompletableFuture<Profile> profileFuture;
        if (resolvers.stream().anyMatch(Resolver::requiresUuid) && refreshUuid) {
            profileFuture = profile.refreshUuidAsync()
                                    .exceptionally(SkinsClient.getErrorHandler()
                                                           .andReturn(profile));
        } else {
            profileFuture = CompletableFuture.completedFuture(profile);
        }

        val errorHandler = SkinsClient.getErrorHandler();
        val defaultTextures = DefaultSkinHelper.getSkinTextures(profile.getId());
        val textures = new SkinTextures(
                defaultTextures.texture(),
                null,
                defaultTextures.capeTexture(),
                defaultTextures.elytraTexture(),
                defaultTextures.model(),
                true
        );
        val future = profileFuture.thenApplyAsync(profile1 -> {
            val handler = errorHandler.<Enumerated<PlayerLoader>>andReturn(null);
            val futures = dispatcher.resolveAsync(resolvers, profile1)
                                  // Added error handling in all futures
                                  .map(f -> f.exceptionally(handler));

            return dispatcher.fetchTexturesAsync(futures).join();
        }).orTimeout(timeout, TimeUnit.MILLISECONDS).thenApplyAsync(result -> {
            if (result != null) {
                for (val texture : result) {
                    this.loadTextures(texture, textures, profile);
                }
            }

            return textures;
        }).exceptionally(errorHandler.andReturn(textures));

        cir.setReturnValue(future);
    }

    @Unique
    @SneakyThrows
    private void loadTextures(
            @NotNull TypedTexture result,
            SkinTextures textures,
            @NotNull Profile profile) {
        val texture = result.getTexture();
        val metadata = texture.getMetadata();
        val textureId = new TextureIdentifier(
                profile.getName(),
                result.getType()
        );

        TextureLoader.create(texture).getTexture(textureId, id -> {
            switch (result.getType()) {
                case SKIN -> {
                    textures.texture = id;

                    if (metadata != null) {
                        textures.model = SkinTextures.Model.fromName(metadata.getModel());
                    }
                }
                case CAPE -> textures.capeTexture = id;
            }
        });
    }

    @Unique
    @Override
    public void skins$refresh() {
        cache.invalidateAll();
    }
}
