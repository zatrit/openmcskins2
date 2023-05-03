package net.zatrit.skins;

import lombok.Getter;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.zatrit.skins.cache.AssetCacheProvider;
import net.zatrit.skins.config.HostEntry;
import net.zatrit.skins.config.SkinsConfig;
import net.zatrit.skins.lib.Skins;
import net.zatrit.skins.lib.enumtypes.HashFunc;
import net.zatrit.skins.lib.resolver.MojangResolver;
import net.zatrit.skins.lib.resolver.NamedHTTPResolver;
import net.zatrit.skins.lib.resolver.Resolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SkinsClient implements ModInitializer {
    static @Getter Skins skins;

    @Override
    public void onInitialize() {
        final var client = MinecraftClient.getInstance();

        AutoConfig.register(SkinsConfig.class, Toml4jConfigSerializer::new);

        final var configHolder = AutoConfig.getConfigHolder(SkinsConfig.class);
        final var config = configHolder.get();
        final var path = ((HasPath) client);

        skins = Skins.builder()
                .hashFunc(HashFunc.MURMUR3)
                .cacheProvider(config.cacheTextures ?
                        new AssetCacheProvider(path) :
                        null)
                .build();

        final var resolvers = config.hosts.stream()
                .map(this::resolverFromEntry)
                .toList();

        for (var resolver : resolvers) {
            System.out.println(resolver);
        }
    }

    public @Nullable Resolver resolverFromEntry(@NotNull HostEntry entry) {
        final var props = entry.getProperties();

        try {
            return switch (entry.getType()) {
                case MOJANG -> new MojangResolver(getSkins());
                case NAMED_HTTP -> new NamedHTTPResolver(getSkins(),
                        (String) props.get("baseUrl"));
            };
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
