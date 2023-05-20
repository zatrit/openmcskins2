package net.zatrit.skins.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.zatrit.skins.SkinsClient;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.resolver.MojangResolver;
import net.zatrit.skins.lib.resolver.NamedHTTPResolver;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Resolvers {
    public static @Nullable Resolver resolverFromEntry(@NotNull HostEntry entry) {
        final var props = entry.getProperties();
        final var config = SkinsClient.getSkinsConfig();

        try {
            return switch (entry.getType()) {
                case MOJANG -> new MojangResolver(config);
                case NAMED_HTTP -> {
                    final var baseUrl = (String) props.get("base_url");
                    Validate.notNull(baseUrl);

                    yield new NamedHTTPResolver(config, baseUrl);
                }
            };
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
