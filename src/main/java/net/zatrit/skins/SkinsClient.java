package net.zatrit.skins;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.val;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;
import net.zatrit.skins.accessor.HasAssetPath;
import net.zatrit.skins.accessor.HasPlayerListEntry;
import net.zatrit.skins.accessor.Refreshable;
import net.zatrit.skins.cache.AssetCacheProvider;
import net.zatrit.skins.config.Resolvers;
import net.zatrit.skins.config.SkinsConfig;
import net.zatrit.skins.config.TomlConfigHolder;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.Skinlib;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.util.ExceptionConsumer;
import net.zatrit.skins.util.ExceptionConsumerImpl;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SkinsClient implements ClientModInitializer {
    private static final @Getter List<Resolver> resolvers = new ArrayList<>();
    private static final @Getter HashFunction hashFunction = Hashing.murmur3_128();
    private static @Getter TomlConfigHolder<SkinsConfig> configHolder;
    private static @Getter Config loaderConfig;
    private static @Getter Skinlib skinlib;
    private static @Getter HttpClient httpClient;
    private static @Getter ExceptionConsumer<Void> errorHandler = new ExceptionConsumerImpl(
            false);

    public static boolean refresh() {
        val client = MinecraftClient.getInstance();
        if (client.world != null) {
            client.world.getPlayers().stream()
                    .map(t -> ((HasPlayerListEntry) t).getPlayerInfo()).filter(
                            Objects::nonNull)
                    .forEach(e -> ((Refreshable) e).skins$refresh());

            return true;
        }
        return false;
    }

    private void applyConfig(@NotNull SkinsConfig config) {
        val path = (HasAssetPath) MinecraftClient.getInstance();

        errorHandler = new ExceptionConsumerImpl(config.isVerboseLogs());

        resolvers.clear();
        resolvers.addAll(config.getHosts().stream().parallel()
                                 .map(Resolvers::resolverFromEntry)
                                 .filter(Objects::nonNull).toList());

        val loaderConfig = getLoaderConfig();

        loaderConfig.setCacheProvider(config.isCacheTextures() ?
                                              new AssetCacheProvider(path) :
                                              null);

        if (config.isRefreshOnConfigSave()) {
            refresh();
        }
    }

    @Override
    public void onInitializeClient() {
        loaderConfig = new Config();
        skinlib = new Skinlib(loaderConfig);

        val configPath = FabricLoader.getInstance().getConfigDir().resolve(
                "openmcskins.toml");

        configHolder = new TomlConfigHolder<>(configPath, new SkinsConfig());
        configHolder.addSaveListener(this::applyConfig);
        configHolder.load();

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(new ElytraTextureFix(SkinsClient::refresh));

        this.applyConfig(configHolder.getConfig());

        val commands = new SkinsCommands(
                configHolder,
                (HasAssetPath) MinecraftClient.getInstance()
        );

        ClientCommandRegistrationCallback.EVENT.register(commands);

        httpClient = HttpClient.newBuilder().executor(loaderConfig.getExecutor())
                             .build();
    }
}
