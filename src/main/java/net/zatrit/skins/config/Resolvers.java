package net.zatrit.skins.config;

import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.zatrit.skins.FallbackResolver;
import net.zatrit.skins.SkinsClient;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.resolver.*;
import net.zatrit.skins.lib.resolver.capes.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.andreinc.aleph.AlephFormatter.str;

@UtilityClass
public class Resolvers {
    @SuppressWarnings("unchecked")
    public static @Nullable Resolver resolverFromEntry(
        @NotNull HostEntry entry) {
        val props = entry.getProperties();
        val config = SkinsClient.getSkinlibConfig();

        try {
            switch (entry.getType()) {
                case GEYSER:
                    var floodgate_prefix = Collections.singletonList(".");
                    if (props != null) {
                        val value = props.get("floodgate_prefix");

                        if (value instanceof List<?>) {
                            floodgate_prefix = (List<String>) value;
                        } else if (value instanceof String) {
                            floodgate_prefix = Collections.singletonList((String) value);
                        }
                    }
                    return new GeyserResolver(config, floodgate_prefix);
                case FALLBACK:
                    return new FallbackResolver(
                        config,
                        MinecraftClient.getInstance().getSessionService()
                    );
                case MOJANG:
                    return new MojangResolver(config);
                case MINECRAFT_CAPES:
                    return new MinecraftCapesResolver(config);
                case FIVEZIG:
                    return new FiveZigResolver(config);
                case LIQUID_BOUNCE:
                    return new LiquidBounceResolver(config);
                case METEOR:
                    return new MeteorResolver(config);
                case NAMED_HTTP:
                case OPTIFINE:
                case VALHALLA:
                case DIRECT:
                    val baseUrl = (String) props.get("base_url");

                    switch (entry.getType()) {
                        case OPTIFINE:
                            return new OptifineResolver(config, baseUrl);
                        case VALHALLA:
                            return new ValhallaResolver(config, baseUrl);
                        case NAMED_HTTP:
                            return new NamedHTTPResolver(config, baseUrl);
                        case DIRECT:
                            val types = ((List<String>) props.get("types")).stream()
                                .map(TextureType::valueOf)
                                .collect(Collectors.toList());
                            return new DirectResolver(config, baseUrl, types);
                    }
                case LOCAL:
                    val directoryPattern = (String) props.get("directory");
                    val directory = Paths.get(str(directoryPattern).arg(
                        "configDir",
                        FabricLoader.getInstance().getConfigDir()
                    ).fmt());

                    return new LocalResolver(config, directory);
                case WURST:
                    return new WurstResolver(config);
            }
        } catch (Exception ex) {
            SkinsClient.getErrorHandler().accept(ex);
        }
        return null;
    }
}
