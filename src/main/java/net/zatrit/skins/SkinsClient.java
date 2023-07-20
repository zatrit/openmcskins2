package net.zatrit.skins;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.SneakyThrows;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.zatrit.skins.cache.AssetCacheProvider;
import net.zatrit.skins.config.Resolvers;
import net.zatrit.skins.config.SkinsConfig;
import net.zatrit.skins.config.TomlConfigInstance;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.SkinLoader;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.util.ExceptionConsumer;
import net.zatrit.skins.util.ExceptionConsumerImpl;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SkinsClient implements ClientModInitializer {
    private static @Getter TomlConfigInstance<SkinsConfig> configInstance;
    private static @Getter Config loaderConfig;
    private static @Getter SkinLoader skinLoader;
    private static @Getter HttpClient httpClient;
    private static @Getter ExceptionConsumer<Void> errorHandler;
    private static final @Getter List<Resolver> resolvers = new ArrayList<>();
    private static final @Getter HashFunction hashFunction = Hashing.murmur3_128();

    public void applyConfig(@NotNull SkinsConfig config) {
        final var path = (AssetPathProvider) MinecraftClient.getInstance();

        errorHandler = new ExceptionConsumerImpl(config.verboseLogs);

        resolvers.clear();
        resolvers.addAll(config.hosts.stream().parallel()
                                 .map(Resolvers::resolverFromEntry)
                                 .filter(Objects::nonNull).toList());

        final var loaderConfig = getLoaderConfig();

        loaderConfig.setCacheProvider(config.cacheTextures ?
                                              new AssetCacheProvider(path) :
                                              null);
        loaderConfig.setLoaderTimeout(config.loaderTimeout);
    }

    @SneakyThrows
    @Override
    public void onInitializeClient() {
        SkinsClient.loaderConfig = Config.builder().build();
        skinLoader = new SkinLoader(SkinsClient.loaderConfig);

        final var configPath = FabricLoader.getInstance().getConfigDir()
                                       .resolve("openmcskins.toml");

        configInstance = new TomlConfigInstance<>(
                configPath.toFile(),
                new SkinsConfig()
        );

        configInstance.addSaveListener(this::applyConfig);
        configInstance.load();

        this.applyConfig(configInstance.getConfig());

        final var commands = new SkinsCommands(
                configInstance,
                MinecraftClient.getInstance()
        );

        ClientCommandRegistrationCallback.EVENT.register(commands);

        httpClient = HttpClient.newBuilder()
                             .executor(SkinsClient.loaderConfig.getExecutor())
                             .build();
    }
}
