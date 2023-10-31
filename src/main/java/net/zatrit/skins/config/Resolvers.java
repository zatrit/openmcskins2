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

import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.andreinc.aleph.AlephFormatter.str;

@UtilityClass
public class Resolvers {
    public static @Nullable Resolver resolverFromEntry(
            @NotNull HostEntry entry) {
        val props = entry.getProperties();
        val config = SkinsClient.getLoaderConfig();

        try {
            switch (entry.getType()) {
                case GEYSER:
                    return new GeyserResolver(
                            config,
                            (String) Objects.requireNonNull(
                                    props.get(
                                            "floodgate_prefix"))
                    );
                case FALLBACK:
                    return new FallbackResolver(
                            config,
                            MinecraftClient.getInstance()
                                    .getSessionService()
                    );
                case MOJANG:
                    return new MojangResolver(config);
                case MINECRAFT_CAPES:
                    return new MinecraftCapesResolver(config);
                case FIVEZIG:
                    return new FiveZigResolver(config);
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
                            @SuppressWarnings("unchecked")
                            val types = ((List<String>) props.get("types")).stream()
                                    .map(TextureType::valueOf)
                                    .collect(Collectors.toList());
                            return new DirectResolver(config, baseUrl, types);
                    }
                case LOCAL:
                    val directoryPattern = (String) props.get("directory");
                    val directory = Paths.get(str(directoryPattern).arg(
                            "configDir",
                            FabricLoader.getInstance()
                                    .getConfigDir()
                    ).fmt());

                    return new LocalResolver(config, directory);
            }
        } catch (Exception ex) {
            SkinsClient.getErrorHandler().accept(ex);
        }
        return null;
    }
}
