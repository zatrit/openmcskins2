package net.zatrit.skins;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.zatrit.skins.cache.AssetCacheProvider;
import net.zatrit.skins.config.ConfigHolder;
import net.zatrit.skins.config.Resolvers;
import net.zatrit.skins.config.SkinsConfig;
import net.zatrit.skins.config.TomlConfigHolder;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.SkinLoader;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.api.SkinLayer;
import net.zatrit.skins.util.CallbackResourceReloadListener;
import net.zatrit.skins.util.ExceptionConsumer;
import net.zatrit.skins.util.ExceptionConsumerImpl;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SkinsClient implements ClientModInitializer {
    private static final @Getter List<Resolver> resolvers = new ArrayList<>();
    private static final @Getter HashFunction hashFunction = Hashing.murmur3_128();
    private static final @Getter Refresher refresher = new Refresher();
    private static @Getter ConfigHolder<SkinsConfig> configHolder;
    private static @Getter Config loaderConfig;
    private static @Getter SkinLoader skinLoader;
    private static @Getter HttpClient httpClient;
    private static @Getter ExceptionConsumer<Void> errorHandler = new ExceptionConsumerImpl(
            false);

    private void applyConfig(@NotNull SkinsConfig config) {
        val path = (AssetPathProvider) MinecraftClient.getInstance();

        errorHandler = new ExceptionConsumerImpl(config.verboseLogs);

        resolvers.clear();
        resolvers.addAll(config.hosts.stream().parallel()
                                 .map(Resolvers::resolverFromEntry)
                                 .filter(Objects::nonNull).toList());

        val loaderConfig = getLoaderConfig();

        loaderConfig.setCacheProvider(config.cacheTextures ?
                                              new AssetCacheProvider(path) :
                                              null);

        if (config.isRefreshOnConfigSave()) {
            refresh();
        }
    }

    private void applyElytraTextureFix(@NotNull ResourceManager manager) {
        val elytraId = new Identifier("textures/entity/elytra.png");
        try (val stream = manager.open(elytraId)) {
            val elytraImage = ImageIO.read(stream);

            SkinLayer.CAPE_LAYER.setBackgroundTexture(elytraImage);
        } catch (IOException e) {
            getErrorHandler().accept(e);
        }
    }

    private void refresh() {
        val client = MinecraftClient.getInstance();
        if (client.world != null) {
            getRefresher().refresh(client.world.getPlayers());
        }
    }

    @SneakyThrows
    @Override
    public void onInitializeClient() {
        SkinsClient.loaderConfig = Config.builder().build();
        skinLoader = new SkinLoader(SkinsClient.loaderConfig,
                SkinLayer.DEFAULT_LAYERS
        );

        val configPath = FabricLoader.getInstance().getConfigDir()
                                       .resolve("openmcskins.toml");

        configHolder = new TomlConfigHolder<>(configPath.toFile(),
                new SkinsConfig()
        );
        configHolder.addSaveListener(this::applyConfig);
        configHolder.load();

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(new CallbackResourceReloadListener(
                        "elytra_texture_fix",
                        manager -> {
                            applyElytraTextureFix(manager);
                            refresh();
                        }
                ));

        this.applyConfig(configHolder.getConfig());

        val commands = new SkinsCommands(configHolder,
                MinecraftClient.getInstance()
        );

        ClientCommandRegistrationCallback.EVENT.register(commands);

        httpClient = HttpClient.newBuilder().executor(loaderConfig.getExecutor())
                             .build();
    }
}
