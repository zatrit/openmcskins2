package net.zatrit.skins;

import com.moandjiezana.toml.Toml;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.zatrit.skins.accessor.HasAssetPath;
import net.zatrit.skins.config.HostEntry;
import net.zatrit.skins.config.SkinsConfig;
import net.zatrit.skins.util.command.*;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.zatrit.skins.cache.AssetCacheProvider.CACHE_DIR;
import static net.zatrit.skins.lib.util.SneakyLambda.sneaky;
import static net.zatrit.skins.util.command.CommandUtil.argument;
import static net.zatrit.skins.util.command.CommandUtil.literal;

@RequiredArgsConstructor
public class SkinsCommands {
    private final ConfigHolder<SkinsConfig> configHolder;
    private final HasAssetPath assetPath;
    private @Nullable CompletableFuture<Void> cleanupFuture;

    public void register(
        @NotNull CommandDispatcher<FabricClientCommandSource> dispatcher) {
        val presetsPath = FabricLoader.getInstance().getConfigDir().resolve(
            "openmcskins");

        val presetsType = new FileArgumentType(new FileProvider[]{
            new IndexedResourceProvider(
                "presets",
                getClass().getClassLoader()
            ), new DirectoryFileProvider(presetsPath)
        }, "toml");
        presetsType.refresh();

        dispatcher.register(registerCommand(
            presetsType,
            literal("openmcskins")
        ));
        dispatcher.register(registerCommand(presetsType, literal("omcs")));
    }

    @Contract("_, _ -> param2")
    private @NotNull LiteralArgumentBuilder<FabricClientCommandSource> registerCommand(
        FileArgumentType presetsType,
        @NotNull LiteralArgumentBuilder<FabricClientCommandSource> literal) {
        literal
            // omcs refresh
            .then(literal("refresh").executes(this::refresh))
            // omcs clean
            .then(literal("clean").executes(this::clean))
            // omcs add (preset (e.g. mojang)) [id]
            .then(literal("add").then(argument(
                "preset",
                presetsType
            ).executes(this::addHost).then(argument(
                "id",
                integer(0)
            ).executes(this::addHost))))
            // omcs list
            .then(literal("list").executes(this::listHosts))
            // omcs remove (id)
            .then(literal("remove").then(argument("id", integer(0)).executes(
                this::removeHost)))
            // omcs move (from) (to)
            .then(literal("move").then(argument("from", integer(0)).then(
                argument("to", integer(0)).executes(this::moveHost))));

        return literal;
    }

    private int refresh(
        @NotNull CommandContext<FabricClientCommandSource> context) {
        if (SkinsClient.refresh()) {
            return 0;
        } else {
            context.getSource().sendError(new TranslatableText(
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
        int id = 0;

        try {
            id = context.getArgument("id", Integer.class);
        } catch (IllegalArgumentException ignored) {
        }

        val toml = new Toml().read(stream);
        val entry = toml.to(HostEntry.class);

        if (entry.getType() == null) {
            context.getSource().sendError(new TranslatableText(
                "openmcskins.command.invalidFileFormat"));
            return -1;
        }

        val config = this.configHolder.get();
        config.getHosts().add(id, entry);
        this.configHolder.save();

        context.getSource().sendFeedback(new TranslatableText(
            "openmcskins.command.added",
            entry.toText()
        ));

        return 0;
    }

    private int listHosts(
        @NotNull CommandContext<FabricClientCommandSource> context) {
        val entries = this.configHolder.getConfig().getHosts().stream().map(
            TextUtil.ToText::toText).toArray(Text[]::new);
        val result = new TranslatableText("openmcskins.command.list");

        for (int i = 0; i < entries.length; i++) {
            result.append(new LiteralText("\n").append(new TranslatableText(
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
        val id = context.getArgument("id", Integer.class);

        val config = this.configHolder.get();
        val entry = config.getHosts().remove(id.intValue());
        this.configHolder.save();

        context.getSource().sendFeedback(new TranslatableText(
            "openmcskins.command.removed",
            entry.toText()
        ));

        return 0;
    }

    private int moveHost(
        @NotNull CommandContext<FabricClientCommandSource> context) {
        val from = context.getArgument("from", Integer.class);
        val to = context.getArgument("to", Integer.class);

        val config = this.configHolder.get();
        val entry = config.getHosts().remove(from.intValue());
        config.getHosts().add(to, entry);
        this.configHolder.save();

        context.getSource().sendFeedback(new TranslatableText(
            "openmcskins.command.moved",
            TextUtil.formatNumber(from),
            TextUtil.formatNumber(to)
        ));

        return 0;
    }

    @SneakyThrows
    @SuppressWarnings("resource")
    private int clean(
        @NotNull CommandContext<FabricClientCommandSource> context) {
        if (cleanupFuture != null && !cleanupFuture.isDone()) {
            context.getSource().sendError(new TranslatableText(
                "openmcskins.command.cleanupAlready"));
            return -1;
        }

        cleanupFuture = CompletableFuture.<Void>supplyAsync(sneaky(() -> {
            Files.list(Paths.get(assetPath.getAssetPath()).resolve(CACHE_DIR)).map(
                Path::toFile).parallel().forEach(directory -> {
                try {
                    FileUtils.deleteDirectory(directory);
                } catch (IOException e) {
                    SkinsClient.getErrorHandler().accept(e);
                }
            });

            return null;
        })).whenComplete((r, e) -> {
            if (e == null) {
                context.getSource().sendFeedback(new TranslatableText(
                    "openmcskins.command.cleanupSuccess"));
            } else {
                context.getSource().sendError(new TranslatableText(
                    "openmcskins.command.cleanupFailed",
                    e.getMessage()
                ));
            }
        });

        return 0;
    }
}