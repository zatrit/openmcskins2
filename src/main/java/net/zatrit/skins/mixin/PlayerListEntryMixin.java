package net.zatrit.skins.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import lombok.SneakyThrows;
import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import net.zatrit.skins.SkinsClient;
import net.zatrit.skins.accessor.Refreshable;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.data.TextureResult;
import net.zatrit.skins.texture.TextureIdentifier;
import net.zatrit.skins.texture.TextureLoader;
import net.zatrit.skins.util.TextureTypeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin implements Refreshable {
    @Shadow private boolean texturesLoaded;
    @Shadow @Final
    private Map<MinecraftProfileTexture.Type, Identifier> textures;
    @Shadow @Nullable private String model;

    @Shadow
    public abstract GameProfile getProfile();

    /* I could use @Overwrite the but SpongePowered Mixin
     * wiki says I shouldn't use it when possibly. */
    @Inject(method = "loadTextures", at = @At("HEAD"), cancellable = true)
    public synchronized void loadTextures(@NotNull CallbackInfo ci) {
        ci.cancel();

        if (this.texturesLoaded) {
            return;
        }

        this.texturesLoaded = true;
        this.textures.clear();
        this.applyMetadata(Collections.emptyMap());

        val profile = (Profile) getProfile();
        val skinLoader = SkinsClient.getSkinLoader();
        val resolvers = SkinsClient.getResolvers();

        val config = SkinsClient.getConfigHolder().getConfig();
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

        CompletableFuture<Profile> profileTask;
        if (resolvers.stream().anyMatch(Resolver::requiresUuid) && refreshUuid) {
            profileTask = profile.skins$refreshUuidAsync()
                                  .exceptionallyAsync(SkinsClient.getErrorHandler()
                                                              .andReturn(profile));
        } else {
            profileTask = CompletableFuture.completedFuture(profile);
        }

        profileTask.thenApplyAsync(profile1 -> skinLoader.fetchAsync(
                        resolvers,
                        profile1
                ).join()).orTimeout(timeout, TimeUnit.MILLISECONDS)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        SkinsClient.getErrorHandler().accept(error);
                    }

                    for (val textureResult : result) {
                        this.loadTextureResult(textureResult);
                    }
                }).exceptionallyAsync(SkinsClient.getErrorHandler()
                                              .andReturn(null));
    }

    @Unique
    @SneakyThrows
    private void loadTextureResult(@NotNull TextureResult result) {
        val type = TextureTypeUtil.toAuthlibType(result.getType());

        // Doesn't create a texture if no matching type is found.
        if (type == null) {
            return;
        }

        val texture = result.getTexture();
        val metadata = texture.getMetadata();

        val textureId = new TextureIdentifier(
                getProfile().getName(),
                result.getType()
        );

        TextureLoader.create(texture).getTexture(textureId, id -> {
            this.textures.put(type, id);

            if (metadata != null) {
                this.applyMetadata(metadata);
            }
        });
    }

    @Unique
    public void applyMetadata(@NotNull Map<String, String> metadata) {
        this.model = metadata.getOrDefault("model", this.model);
    }

    @Override
    public void skins$refresh() {
        this.texturesLoaded = false;
    }
}
