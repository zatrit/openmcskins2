package net.zatrit.skins.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import net.fabricmc.loader.api.FabricLoader;
import net.zatrit.skins.SkinsClient;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.resolver.LocalResolver;
import net.zatrit.skins.lib.resolver.MojangResolver;
import net.zatrit.skins.lib.resolver.NamedHTTPResolver;
import net.zatrit.skins.lib.resolver.OptifineResolver;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Resolvers {
    public static @Nullable Resolver resolverFromEntry(@NotNull HostEntry entry) {
        val props = entry.getProperties();
        val config = SkinsClient.getLoaderConfig();

        try {
            return switch (entry.getType()) {
                case MOJANG -> new MojangResolver(config);
                case NAMED_HTTP, OPTIFINE -> {
                    val baseUrl = (String) props.get("base_url");
                    Validate.notNull(baseUrl);

                    yield switch (entry.getType()) {
                        case OPTIFINE -> new OptifineResolver(config, baseUrl);
                        case NAMED_HTTP -> new NamedHTTPResolver(config, baseUrl);
                        default -> null;
                    };
                }
                case LOCAL -> {
                    val directory = (String) props.get("directory");
                    val replaces = new HashMap<String, Object>();
                    replaces.put(
                            "configDir",
                            FabricLoader.getInstance().getConfigDir().toString()
                    );

                    yield new LocalResolver(config, directory, replaces);
                }
            };
        } catch (Exception ex) {
            SkinsClient.getErrorHandler().accept(ex);
            return null;
        }
    }
}
