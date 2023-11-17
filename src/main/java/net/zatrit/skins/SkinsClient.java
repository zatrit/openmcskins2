package net.zatrit.skins;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import lombok.Getter;
import lombok.val;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;
import net.zatrit.skins.accessor.HasAssetPath;
import net.zatrit.skins.accessor.Refreshable;
import net.zatrit.skins.cache.AssetCacheProvider;
import net.zatrit.skins.config.Resolvers;
import net.zatrit.skins.config.SkinsConfig;
import net.zatrit.skins.config.TomlConfigSerializer;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.TextureDispatcher;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.layer.awt.ImageLayer;
import net.zatrit.skins.lib.layer.awt.LegacySkinLayer;
import net.zatrit.skins.lib.layer.awt.ScaleCapeLayer;
import net.zatrit.skins.util.ExceptionConsumer;
import net.zatrit.skins.util.ExceptionConsumerImpl;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class SkinsClient implements ClientModInitializer {
    private static final @Getter List<Resolver> resolvers = new ArrayList<>();
    private static final @Getter HashFunction hashFunction = Hashing.murmur3_128();
    private static final @Getter ScaleCapeLayer capeLayer = new ScaleCapeLayer();
    private static @Getter ConfigClassHandler<SkinsConfig> configHandler;
    private static @Getter Config skinlibConfig;
    private static @Getter TextureDispatcher dispatcher;
    private static @Getter HttpClient httpClient;
    private static @Getter ExceptionConsumer<Void> errorHandler = new ExceptionConsumerImpl(
            true);

    public static boolean refresh() {
        val provider = MinecraftClient.getInstance().getSkinProvider();
        if (provider instanceof Refreshable refreshable) {
            refreshable.skins$refresh();
            return true;
        }
        return false;
    }

    private void applyConfig(@NotNull SkinsConfig config) {
        val path = (HasAssetPath) MinecraftClient.getInstance();

        errorHandler = new ExceptionConsumerImpl(config.isVerboseLogs());

        resolvers.clear();
        resolvers.addAll(config.getHosts().parallelStream()
                                 .map(Resolvers::resolverFromEntry)
                                 .filter(Objects::nonNull).toList());

        skinlibConfig.setCacheProvider(config.isCacheTextures() ?
                                               new AssetCacheProvider(path) :
                                               null);

        if (config.isRefreshOnConfigSave()) {
            refresh();
        }
    }

    @Override
    public void onInitializeClient() {
        skinlibConfig = new Config();
        dispatcher = new TextureDispatcher(skinlibConfig);

        skinlibConfig.setLayers(List.of(new ImageLayer(
                Collections.singleton(capeLayer),
                // Applies only to static cape textures.
                texture -> {
                    val metadata = texture.getTexture().getMetadata();
                    val cape = texture.getType() == TextureType.CAPE;

                    if (metadata == null) {
                        return cape;
                    }

                    return cape && !metadata.isAnimated();
                }
        ), new ImageLayer(
                Collections.singleton(new LegacySkinLayer()),
                texture -> texture.getType() == TextureType.SKIN
        )));

        configHandler = ConfigClassHandler.createBuilder(SkinsConfig.class)
                .serializer(handler1 -> {
                    val serializer = new TomlConfigSerializer<>(
                            FabricLoader.getInstance()
                                    .getConfigDir()
                                    .resolve(
                                            "openmcskins.toml"),
                            handler1
                    );
                    serializer.addSaveListener(this::applyConfig);
                    return serializer;
                }).build();
        configHandler.load();

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(new ElytraTextureFix());

        this.applyConfig(configHandler.instance());

        val commands = new SkinsCommands(
                configHandler,
                (HasAssetPath) MinecraftClient.getInstance()
        );

        ClientCommandRegistrationCallback.EVENT.register(commands);

        httpClient = HttpClient.newBuilder()
                .executor(skinlibConfig.getExecutor()).build();
    }
}
