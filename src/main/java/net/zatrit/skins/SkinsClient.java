package net.zatrit.skins;

import com.google.common.collect.Lists;
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
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.layer.awt.LegacySkinLayer;
import net.zatrit.skins.lib.layer.awt.ScaleCapeLayer;
import net.zatrit.skins.util.ExceptionConsumer;
import net.zatrit.skins.util.ExceptionConsumerImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class SkinsClient implements ClientModInitializer {
    private static final @Getter List<Resolver> resolvers = new ArrayList<>();
    @SuppressWarnings("UnstableApiUsage")
    private static final @Getter HashFunction hashFunction = Hashing.murmur3_128();
    private static final @Getter ScaleCapeLayer capeLayer = new ScaleCapeLayer();
    private static @Getter ConfigHolder<SkinsConfig> configHolder;
    private static @Getter Config skinlibConfig;
    private static @Getter TextureDispatcher dispatcher;
    private static @Getter ExceptionConsumer<Void> errorHandler = new ExceptionConsumerImpl(
        false);

    public static boolean refresh() {
        for (Resolver resolver : getResolvers()) {
            resolver.refresh();
        }

        val client = MinecraftClient.getInstance();
        if (client.world != null) {
            for (val entity : client.world.getPlayers()) {
                val entry = entity.getPlayerListEntry();
                if (entry != null) {
                    ((Refreshable) entry).skins$refresh();
                }
            }

            return true;
        }
        return false;
    }

    private ActionResult applyConfig(
        ConfigHolder<SkinsConfig> holder, @NotNull SkinsConfig config) {
        val path = (HasAssetPath) MinecraftClient.getInstance();

        errorHandler = new ExceptionConsumerImpl(config.isVerboseLogs());

        resolvers.clear();

        for (val hostEntry : config.getHosts()) {
            val resolver = Resolvers.resolverFromEntry(hostEntry);
            if (resolver != null) {
                resolvers.add(resolver);
            }
        }

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

        skinlibConfig.setLayers(List.of(capeLayer, new LegacySkinLayer()));

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
    }
}
