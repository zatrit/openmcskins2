package net.zatrit.skins;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.val;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.ActionResult;
import net.zatrit.skins.accessor.HasAssetPath;
import net.zatrit.skins.accessor.Refreshable;
import net.zatrit.skins.cache.AssetCacheProvider;
import net.zatrit.skins.config.Resolvers;
import net.zatrit.skins.config.SkinsConfig;
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
    @SuppressWarnings("UnstableApiUsage")
    private static final @Getter HashFunction hashFunction = Hashing.murmur3_128();
    private static @Getter ConfigHolder<SkinsConfig> configHolder;
    private static final @Getter ScaleCapeLayer capeLayer = new ScaleCapeLayer();
    private static @Getter Config skinlibConfig;
    private static @Getter TextureDispatcher dispatcher;
    private static @Getter HttpClient httpClient;
    private static @Getter ExceptionConsumer<Void> errorHandler = new ExceptionConsumerImpl(
            false);

    public static boolean refresh() {
        getResolvers().forEach(Resolver::refresh);

        val provider = MinecraftClient.getInstance().getSkinProvider();

        if (provider instanceof Refreshable refreshable) {
            refreshable.skins$refresh();
            return true;
        }

        return false;
    }

    private ActionResult applyConfig(
            ConfigHolder<SkinsConfig> holder, @NotNull SkinsConfig config) {
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

        return ActionResult.SUCCESS;
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

        configHolder = AutoConfig.register(
                SkinsConfig.class,
                Toml4jConfigSerializer::new
        );
        configHolder.registerSaveListener(this::applyConfig);
        configHolder.registerLoadListener(this::applyConfig);
        configHolder.load();

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(new ElytraTextureFix());

        val commands = new SkinsCommands(
                configHolder,
                (HasAssetPath) MinecraftClient.getInstance()
        );
        commands.register(ClientCommandManager.DISPATCHER);

        httpClient = HttpClient.newBuilder()
                .executor(skinlibConfig.getExecutor()).build();
    }
}
