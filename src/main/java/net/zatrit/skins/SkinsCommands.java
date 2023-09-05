package net.zatrit.skins;

import com.moandjiezana.toml.Toml;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.zatrit.skins.config.ConfigHolder;
import net.zatrit.skins.config.HostEntry;
import net.zatrit.skins.config.SkinsConfig;
import net.zatrit.skins.util.command.*;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.zatrit.skins.util.command.CommandUtil.argument;
import static net.zatrit.skins.util.command.CommandUtil.literal;

@AllArgsConstructor
public class SkinsCommands {
    private final ConfigHolder<SkinsConfig> configHolder;

    public void register(
            @NotNull CommandDispatcher<FabricClientCommandSource> dispatcher) {
        val presetsPath = FabricLoader.getInstance().getConfigDir().resolve(
                "openmcskins");

        val presetsType = new FileArgumentType(new FileProvider[]{
                new IndexedResourceProvider(
                        "presets",
                        getClass().getClassLoader()
                ),
                new DirectoryFileProvider(presetsPath)
        }, "toml");
        presetsType.refresh();

        val command = literal("openmcskins")
                              // omcs refresh
                              .then(literal("refresh").executes(this::refresh))
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
                              .then(literal("remove").then(argument(
                                      "id",
                                      integer(0)
                              ).executes(this::removeHost)))
                              // omcs move (from) (to)
                              .then(literal("move").then(argument(
                                      "from",
                                      integer(0)
                              ).then(argument(
                                      "to",
                                      integer(0)
                              ).executes(this::moveHost))));

        dispatcher.register(command);
        dispatcher.register(literal("omcs").redirect(command.build()));
    }

    private int refresh(@NotNull CommandContext<FabricClientCommandSource> context) {
        if (SkinsClient.refresh()) {
            return 0;
        } else {
            context.getSource().sendError(new TranslatableText(
                    "openmcskins.command.unableToRefresh"));
            return -1;
        }
    }

    @SneakyThrows
    public int addHost(@NotNull CommandContext<FabricClientCommandSource> context) {
        @Cleanup val stream = context.getArgument("preset", InputStream.class);
        int id = 0;

        try {
            id = context.getArgument("id", Integer.class);
        } catch (IllegalArgumentException ignored) {
        }

        val finalId = id;
        val toml = new Toml().read(stream);
        val entry = toml.to(HostEntry.class);

        if (entry.getType() == null) {
            context.getSource().sendError(new TranslatableText(
                    "openmcskins.command" + ".invalidFileFormat"));
            return -1;
        }

        this.configHolder.patchConfig(config -> {
            config.hosts.add(finalId, entry);
            return null;
        });

        context.getSource().sendFeedback(new TranslatableText(
                "openmcskins.command.added",
                entry.toText()
        ));

        return 0;
    }

    private int listHosts(@NotNull CommandContext<FabricClientCommandSource> context) {
        val entries = this.configHolder.getConfig().getHosts().stream().map(
                TextUtil.ToText::toText).toArray(Text[]::new);
        var result = new TranslatableText("openmcskins.command.list");

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

    private int removeHost(@NotNull CommandContext<FabricClientCommandSource> context) {
        val id = context.getArgument("id", Integer.class);

        @SuppressWarnings("CodeBlock2Expr")
        val entry = this.configHolder.patchConfig(config -> {
            return config.getHosts().remove(id.intValue());
        });

        context.getSource().sendFeedback(new TranslatableText(
                "openmcskins.command.removed",
                entry.toText()
        ));

        return 0;
    }

    private int moveHost(@NotNull CommandContext<FabricClientCommandSource> context) {
        val from = context.getArgument("from", Integer.class);
        val to = context.getArgument("to", Integer.class);

        this.configHolder.patchConfig(config -> {
            val entry = config.getHosts().remove(from.intValue());
            config.getHosts().add(to, entry);

            return null;
        });

        context.getSource().sendFeedback(new TranslatableText(
                "openmcskins.command.moved",
                TextUtil.formatNumber(from),
                TextUtil.formatNumber(to)
        ));

        return 0;
    }
}