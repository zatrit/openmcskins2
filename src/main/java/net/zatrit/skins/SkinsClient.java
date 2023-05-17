package net.zatrit.skins;

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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class SkinsClient implements ClientModInitializer {
    static @Getter Config config;
    static @Getter SkinLoader skinLoader;
    static @Getter HttpClient httpClient;
    static @Getter List<Resolver> resolvers = Collections.emptyList();

    public ActionResult updateConfig(
            ConfigHolder<SkinsConfig> holder, @NotNull SkinsConfig config) {
        var path = (AssetPathProvider) MinecraftClient.getInstance();

        resolvers = config.hosts.stream()
                            .map(Resolvers::resolverFromEntry)
                            .filter(Objects::nonNull)
                            .toList();

        var loaderConfig = getConfig();

        loaderConfig.setCacheProvider(config.cacheTextures ?
                                          new AssetCacheProvider(path) :
                                          null);
        loaderConfig.setLoaderTimeout(config.loaderTimeout);

        return ActionResult.SUCCESS;
    }

    @SneakyThrows
    @Override
    public void onInitializeClient() {
        SkinsClient.config = Config.builder().build();
        skinLoader = new SkinLoader(SkinsClient.config);

        final var configHolder = AutoConfig.register(
                SkinsConfig.class,
                Toml4jConfigSerializer::new
        );
        configHolder.registerSaveListener(this::updateConfig);
        this.updateConfig(configHolder, configHolder.getConfig());

        httpClient = HttpClient.newBuilder()
                             .executor(SkinsClient.config.getExecutor())
                             .followRedirects(HttpClient.Redirect.NEVER)
                             .build();
    }
}
