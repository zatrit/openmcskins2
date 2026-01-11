package zatrit.skins;

import com.moandjiezana.toml.Toml;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import zatrit.skins.accessor.HasAssetPath;
import zatrit.skins.config.HostEntry;
import zatrit.skins.config.SkinsConfig;
import zatrit.skins.util.command.*;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static zatrit.skins.util.command.CommandUtil.argument;
import static zatrit.skins.util.command.CommandUtil.literal;
import static zatrit.skins.cache.AssetCache.CACHE_ID;

@RequiredArgsConstructor
public class SkinsCommands implements ClientCommandRegistrationCallback {
    private final ConfigClassHandler<SkinsConfig> configHolder;
    private final HasAssetPath assetPath;
    private @Nullable CompletableFuture<Void> cleanupFuture;

    @Override
    public void register(
        @NotNull CommandDispatcher<FabricClientCommandSource> dispatcher,
        CommandRegistryAccess registryAccess) {
        val presetsPath = FabricLoader.getInstance().getConfigDir().resolve(
            "openmcskins");

        val presetsType = new FileArgumentType(new FileProvider[]{
            new IndexedResourceProvider(
                "presets",
                getClass().getClassLoader()
            ), new DirectoryFileProvider(presetsPath)
        }, "toml");
        presetsType.refresh();

        val command = literal("openmcskins")
            // omcs refresh
            .then(literal("refresh").executes(this::refresh))
            // omcs clean
            .then(literal("clean").executes(this::clean))
            // omcs add (preset (e.g. mojang)) [pos]
            .then(literal("add").then(argument("preset", presetsType).executes(
                this::addHost).then(argument(
                "pos",
                integer(0)
            ).executes(this::addHost))))
            // omcs list
            .then(literal("list").executes(this::listHosts))
            // omcs remove (pos)
            .then(literal("remove").then(argument("pos", integer(0)).executes(
                this::removeHost)))
            // omcs move (from) (to)
            .then(literal("move").then(argument(
                "from",
                integer(0)
            ).then(argument(
                "to",
                integer(0)
            ).executes(this::moveHost))))
            // omcs (blacklist | whitelist) ->
            //  add (name | uuid)
            //  remove (name | uuid)
            //  clear
            .then(filterListArgument("blacklist", List.of())).then(
                filterListArgument("whitelist", List.of()));

        dispatcher.register(command);
        dispatcher.register(literal("omcs").redirect(command.build()));
    }

    private int refresh(
        @NotNull CommandContext<FabricClientCommandSource> context) {
        if (SkinsClient.refresh()) {
            return 0;
        } else {
            context.getSource().sendError(Text.translatable(
                "openmcskins.command.unableToRefresh"));
            return -1;
        }
    }

    @SneakyThrows
    public int addHost(
        @NotNull CommandContext<FabricClientCommandSource> context) {
        @Cleanup val stream = Files.newInputStream(context.getArgument(
            "preset",
            Path.class
        ));
        int pos = 0;

        try {
            pos = context.getArgument("pos", Integer.class);
        } catch (IllegalArgumentException ignored) {
        }

        val toml = new Toml().read(stream);
        val entry = toml.to(HostEntry.class);

        if (entry.getType() == null) {
            context.getSource().sendError(Text.translatable(
                "openmcskins.command.invalidFileFormat"));
            return -1;
        }

        val config = this.configHolder.instance();
        config.getHosts().add(pos, entry);
        this.configHolder.save();

        context.getSource().sendFeedback(Text.translatable(
            "openmcskins.command.added",
            entry.toText()
        ));

        return 0;
    }

    private int listHosts(
        @NotNull CommandContext<FabricClientCommandSource> context) {
        val entries = this.configHolder.instance().getHosts().stream().map(
            TextUtil.ToText::toText).toArray(Text[]::new);
        var result = Text.translatable("openmcskins.command.list");

        for (int i = 0; i < entries.length; i++) {
            result.append(Text.literal("\n").append(Text.translatable(
                "openmcskins.command.listEntry",
                TextUtil.formatNumber(i),
                entries[i]
            )));
        }

        context.getSource().sendFeedback(result);

        return 0;
    }

    private int removeHost(
        @NotNull CommandContext<FabricClientCommandSource> context) {
        val pos = context.getArgument("pos", Integer.class);

        val config = this.configHolder.instance();
        val entry = config.getHosts().remove(pos.intValue());
        this.configHolder.save();

        context.getSource().sendFeedback(Text.translatable(
            "openmcskins.command.removed",
            entry.toText()
        ));

        return 0;
    }

    private int moveHost(
        @NotNull CommandContext<FabricClientCommandSource> context) {
        val from = context.getArgument("from", Integer.class);
        val to = context.getArgument("to", Integer.class);

        val config = this.configHolder.instance();
        val entry = config.getHosts().remove(from.intValue());
        config.getHosts().add(to, entry);
        this.configHolder.save();

        context.getSource().sendFeedback(Text.translatable(
            "openmcskins.command.moved",
            TextUtil.formatNumber(from),
            TextUtil.formatNumber(to)
        ));

        return 0;
    }

    @SneakyThrows
    private int clean(
        @NotNull CommandContext<FabricClientCommandSource> context) {
        if (cleanupFuture != null && !cleanupFuture.isDone()) {
            context.getSource().sendError(Text.translatable(
                "openmcskins.command.cleanupAlready"));
            return -1;
        }

        cleanupFuture = CompletableFuture.supplyAsync(new Supplier<Void>() {
            @Override
            @SneakyThrows
            public Void get() {
                Files.list(assetPath.getAssetPath().resolve(CACHE_ID))
                    .map(Path::toFile).parallel().forEach(directory -> {
                        try {
                            FileUtils.deleteDirectory(directory);
                        } catch (IOException e) {
                            SkinsClient.getErrorHandler().accept(e);
                        }
                    });

                return null;
            }
        }).whenComplete((r, e) -> {
            if (e == null) {
                context.getSource().sendFeedback(Text.translatable(
                    "openmcskins.command.cleanupSuccess"));
            } else {
                context.getSource().sendError(Text.translatable(
                    "openmcskins.command.cleanupFailed",
                    e.getMessage()
                ));
            }
        });

        return 0;
    }

    @Contract(value = "_, _ -> new", pure = true)
    private @NotNull LiteralArgumentBuilder<FabricClientCommandSource> filterListArgument(
        @NotNull String name, @NotNull List<String> list) {
        return literal(name);
    }
}
