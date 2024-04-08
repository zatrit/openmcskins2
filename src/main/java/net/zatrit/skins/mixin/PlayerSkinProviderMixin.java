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
import net.zatrit.skins.accessor.AsyncUUIDRefresher;
import net.zatrit.skins.accessor.Refreshable;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.data.TypedTexture;
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
    private LoadingCache<PlayerSkinProvider.Key, CompletableFuture<SkinTextures>> cache;

    @SneakyThrows
    @Inject(
        at = @At("HEAD"),
        method = "fetchSkinTextures(Lcom/mojang/authlib/GameProfile;)Ljava/util/concurrent/CompletableFuture;",
        cancellable = true)
    public void fetchSkinTextures(
        @NotNull GameProfile profile2,
        @NotNull CallbackInfoReturnable<CompletableFuture<SkinTextures>> cir) {

        cir.setReturnValue(cache.get(new PlayerSkinProvider.Key(
            profile2.getId(),
            null
        ), () -> fetchSkinTextures((Profile) profile2)));
    }

    @Unique
    private CompletableFuture<SkinTextures> fetchSkinTextures(
        @NotNull Profile profile) {
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
            profileFuture = ((AsyncUUIDRefresher) profile).skins$refreshUuid()
                .exceptionally(SkinsClient.getErrorHandler().andReturn(profile));
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

        return profileFuture.thenApplyAsync(profile1 -> {
            val futures = dispatcher.resolveAsync(resolvers, profile1)
                // Added error handling in all futures
                .map(f -> f.exceptionally(errorHandler.andReturn(null)));

            return dispatcher.fetchTexturesAsync(futures).join();
        }).orTimeout(timeout, TimeUnit.MILLISECONDS).thenApplyAsync(result -> {
            if (result != null) {
                for (val texture : result) {
                    this.loadTexture(texture, textures, profile);
                }
            }

            return textures;
        }).exceptionally(errorHandler.andReturn(textures));
    }

    @Unique
    @SneakyThrows
    private void loadTexture(
        @NotNull TypedTexture result, SkinTextures textures,
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

                    if (metadata != null && metadata.getModel() != null) {
                        textures.model = SkinTextures.Model.fromName(metadata.getModel());
                    } else {
                        textures.model = SkinTextures.Model.WIDE;
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
