package net.zatrit.skins;

import com.google.common.hash.Hashing;
import lombok.Getter;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.zatrit.skins.cache.AssetCacheProvider;
import net.zatrit.skins.config.HostEntry;
import net.zatrit.skins.config.SkinsConfig;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.SkinLoader;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.resolver.MojangResolver;
import net.zatrit.skins.lib.resolver.NamedHTTPResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLContext;
import java.net.Authenticator;
import java.net.http.HttpClient;
import java.util.Objects;

public final class SkinsClient implements ModInitializer {
    static @Getter Config skinsConfig;
    static @Getter SkinLoader skinLoader;
    static @Getter HttpClient httpClient;

    @Override
    public void onInitialize() {
        final var client = MinecraftClient.getInstance();

        AutoConfig.register(SkinsConfig.class, Toml4jConfigSerializer::new);

        final var configHolder = AutoConfig.getConfigHolder(SkinsConfig.class);
        final var config = configHolder.get();
        final var path = ((AssetPathProvider) client);

        skinsConfig = Config.builder().cacheProvider(config.cacheTextures ?
                                                     new AssetCacheProvider(path) :
                                                     null).build();
        skinLoader = new SkinLoader(skinsConfig);

        httpClient = HttpClient.newBuilder().executor(skinsConfig.getExecutor())
                             .followRedirects(HttpClient.Redirect.NEVER)
                             .build();

        final var resolvers = config.hosts.stream().map(this::resolverFromEntry)
                                      .filter(Objects::nonNull).toList();

        for (var resolver : resolvers) {
            System.out.println(resolver);
        }
    }

    public @Nullable Resolver resolverFromEntry(@NotNull HostEntry entry) {
        final var props = entry.getProperties();

        try {
            return switch (entry.getType()) {
                case MOJANG -> new MojangResolver(getSkinsConfig());
                case NAMED_HTTP -> new NamedHTTPResolver(getSkinsConfig(),
                        (String) props.get("base_url")
                );
            };
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
