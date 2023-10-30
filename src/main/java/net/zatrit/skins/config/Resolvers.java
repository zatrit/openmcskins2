package net.zatrit.skins.config;

import lombok.experimental.UtilityClass;
import lombok.val;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.zatrit.skins.FallbackResolver;
import net.zatrit.skins.SkinsClient;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.resolver.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import static net.andreinc.aleph.AlephFormatter.str;

@UtilityClass
public final class Resolvers {
    public static @Nullable Resolver resolverFromEntry(@NotNull HostEntry entry) {
        val props = entry.getProperties();
        val config = SkinsClient.getLoaderConfig();

        try {
            return switch (entry.getType()) {
                case FALLBACK -> new FallbackResolver(
                        config,
                        MinecraftClient.getInstance()
                                .getSessionService()
                );
                case FIVEZIG -> new FiveZigResolver(config);
                case MOJANG -> new MojangResolver(config);
                case MINECRAFT_CAPES -> new MinecraftCapesResolver(config);
                case NAMED_HTTP, OPTIFINE, VALHALLA, DIRECT -> {
                    val baseUrl = (String) props.get("base_url");

                    yield switch (entry.getType()) {
                        case OPTIFINE -> new OptifineResolver(config, baseUrl);
                        case VALHALLA -> new ValhallaResolver(config, baseUrl);
                        case NAMED_HTTP -> new NamedHTTPResolver(
                                config,
                                baseUrl
                        );
                        case DIRECT -> {
                            @SuppressWarnings("unchecked")
                            val types = ((List<String>) props.get("types")).stream()
                                                .map(TextureType::valueOf)
                                                .toList();
                            yield new DirectResolver(config, baseUrl, types);
                        }
                        default -> null;
                    };
                }
                case LOCAL -> {
                    val directoryPattern = (String) props.get("directory");
                    val replaces = new HashMap<String, Object>();
                    replaces.put(
                            "configDir",
                            FabricLoader.getInstance().getConfigDir()
                    );

                    val directory = Path.of(str(directoryPattern).args(replaces)
                                                    .fmt());

                    yield new LocalResolver(config, directory);
                }
            };
        } catch (Exception ex) {
            SkinsClient.getErrorHandler().accept(ex);
            return null;
        }
    }
}
