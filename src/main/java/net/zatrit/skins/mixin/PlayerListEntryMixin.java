package net.zatrit.skins.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.zatrit.skins.Refreshable;
import net.zatrit.skins.SkinsClient;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.data.TextureResult;
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
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static net.zatrit.skins.lib.util.SneakyLambda.sneaky;

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

        final var profile = (Profile) getProfile();
        final var skinLoader = SkinsClient.getSkinLoader();
        final var resolvers = SkinsClient.getResolvers();

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

        profileTask.thenApplyAsync(profile1 -> skinLoader.fetchAsync(resolvers,
                profile1
        ).join()).whenComplete(sneaky((result, error) -> {
            if (error != null) {
                error.printStackTrace();
            }

            for (final var textureResult : result) {
                this.loadTextureResult(textureResult);
            }
        }));
    }

    private void loadTextureResult(@NotNull TextureResult result)
            throws IOException {
        final var type = TextureTypeUtil.toAuthlibType(result.getType());

        // Doesn't create a texture if no matching type is found
        if (type == null) {
            return;
        }

        final var texture = result.getTexture();
        final var image = NativeImage.read(new ByteArrayInputStream(texture.getContent()));
        final var playerTexture = new NativeImageBackedTexture(image);
        final var id = MinecraftClient.getInstance().getTextureManager()
                               .registerDynamicTexture("skins", playerTexture);

        this.textures.put(type, id);

        final var metadata = texture.getMetadata();

        if (metadata == null) {
            return;
        }

        this.model = texture.getMetadata().getOrDefault("model", this.model);
    }

    @Override
    public void refresh() {
        this.texturesLoaded = false;
    }
}
