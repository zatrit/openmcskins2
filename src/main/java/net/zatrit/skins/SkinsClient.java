package net.zatrit.skins;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.SneakyThrows;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;
import net.zatrit.skins.cache.AssetCacheProvider;
import net.zatrit.skins.config.Resolvers;
import net.zatrit.skins.config.SkinsConfig;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.SkinLoader;
import net.zatrit.skins.lib.api.Resolver;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SkinsClient implements ClientModInitializer {
    private static @Getter Config skinsConfig;
    private static @Getter SkinLoader skinLoader;
    private static @Getter HttpClient httpClient;
    private static @Getter List<Resolver> resolvers = new ArrayList<>();
    private static @Getter HashFunction hashFunction = Hashing.murmur3_128();

    public ActionResult updateConfig(
            ConfigHolder<SkinsConfig> holder, @NotNull SkinsConfig config) {
        final var path = (AssetPathProvider) MinecraftClient.getInstance();

        resolvers.clear();
        resolvers.addAll(config.hosts.stream()
                                 .parallel()
                                 .map(Resolvers::resolverFromEntry)
                                 .filter(Objects::nonNull)
                                 .toList());

        final var loaderConfig = getSkinsConfig();

        loaderConfig.setCacheProvider(config.cacheTextures ?
                                              new AssetCacheProvider(path) :
                                              null);
        loaderConfig.setLoaderTimeout(config.loaderTimeout);

        return ActionResult.SUCCESS;
    }

    @SneakyThrows
    @Override
    public void onInitializeClient() {
        SkinsClient.skinsConfig = Config.builder().build();
        skinLoader = new SkinLoader(SkinsClient.skinsConfig);

        final var configHolder = AutoConfig.register(SkinsConfig.class,
                Toml4jConfigSerializer::new
        );
        configHolder.registerSaveListener(this::updateConfig);
        this.updateConfig(configHolder, configHolder.getConfig());

        httpClient = HttpClient.newBuilder()
                             .executor(SkinsClient.skinsConfig.getExecutor())
                             .followRedirects(HttpClient.Redirect.NEVER)
                             .build();
    }
}
