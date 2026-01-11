package zatrit.skins.config;

import lombok.experimental.UtilityClass;
import lombok.val;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import zatrit.skins.FallbackResolver;
import zatrit.skins.SkinsClient;
import zatrit.skins.lib.TextureType;
import zatrit.skins.lib.api.Resolver;
import zatrit.skins.lib.resolver.*;
import zatrit.skins.lib.resolver.capes.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static net.andreinc.aleph.AlephFormatter.str;

@UtilityClass
public class Resolvers {
    @SuppressWarnings("unchecked")
    public static @Nullable Resolver resolverFromEntry(
        @NotNull HostEntry entry) {
        val props = entry.getProperties();
        val config = SkinsClient.getSkinlibConfig();

        try {
            return switch (entry.getType()) {
                case GEYSER -> {
                    var floodgatePrefix = Collections.singletonList(".");
                    if (props != null) {
                        val value = props.get("floodgate_prefix");

                        if (value instanceof List<?> prefixes) {
                            floodgatePrefix = (List<String>) prefixes;
                        } else if (value instanceof String prefix) {
                            floodgatePrefix = Collections.singletonList(prefix);
                        }
                    }

                    yield new GeyserResolver(config, floodgatePrefix);
                }
                case FALLBACK -> new FallbackResolver(
                    config,
                    MinecraftClient.getInstance()
                        .getSessionService()
                );
                case FIVEZIG -> new FiveZigResolver(config);
                case LIQUID_BOUNCE -> new LiquidBounceResolver(config);
                case METEOR -> new MeteorResolver(config);
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
                            val types = ((List<String>) props.get("types")).stream()
                                .map(TextureType::valueOf).toList();
                            yield new DirectResolver(config, baseUrl, types);
                        }
                        default -> null;
                    };
                }
                case LOCAL -> {
                    val directoryPattern = (String) props.get("directory");

                    val instance = FabricLoader.getInstance();
                    val directory = Path.of(str(directoryPattern).arg(
                        "configDir",
                        instance.getConfigDir()
                    ).arg(
                        "gameDir",
                        instance.getGameDir()
                    ).fmt());

                    yield new LocalResolver(config, directory);
                }
                case WURST -> new WurstResolver(config);
            };
        } catch (Exception ex) {
            SkinsClient.getErrorHandler().accept(ex);
            return null;
        }
    }
}
