package net.zatrit.skins;

import com.moandjiezana.toml.Toml;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.zatrit.skins.config.ConfigHolder;
import net.zatrit.skins.config.HostEntry;
import net.zatrit.skins.config.SkinsConfig;
import net.zatrit.skins.util.command.*;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.zatrit.skins.util.ConfigUtil.patchConfig;
import static net.zatrit.skins.util.command.CommandUtil.argument;
import static net.zatrit.skins.util.command.CommandUtil.literal;

@AllArgsConstructor
public class SkinsCommands implements ClientCommandRegistrationCallback {
    private final ConfigHolder<SkinsConfig> configInstance;

    @Override
    public void register(
            @NotNull CommandDispatcher<FabricClientCommandSource> dispatcher,
            CommandRegistryAccess registryAccess) {
        val presetsPath = FabricLoader.getInstance().getConfigDir()
                                  .resolve("openmcskins");

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
        if (!SkinsClient.refresh()) {
            context.getSource()
                    .sendError(Text.translatable(
                            "openmcskins.command.unable_to_refresh"));
            return -1;
        }

        return 0;
    }

    @SneakyThrows
    public int addHost(@NotNull CommandContext<FabricClientCommandSource> context) {
        @Cleanup val stream = context.getArgument("preset", InputStream.class);
        int id = 0;

        try {
            id = context.getArgument("id", Integer.class);
        } catch (IllegalArgumentException ignored) {
        }

        final int finalId = id;
        val toml = new Toml().read(stream);
        val entry = toml.to(HostEntry.class);

        if (entry.getType() == null) {
            context.getSource().sendError(Text.translatable(
                    "openmcskins.command.invalid_file_format"));
            return -1;
        }

        patchConfig(this.configInstance, config -> {
            config.hosts.add(finalId, entry);
            return null;
        });

        context.getSource().sendFeedback(Text.translatable(
                "openmcskins.command.added",
                entry.toText()
        ));

        return 0;
    }

    private int listHosts(@NotNull CommandContext<FabricClientCommandSource> context) {
        val entries = this.configInstance.getConfig().getHosts().stream()
                              .map(TextUtil.ToText::toText).toArray(Text[]::new);
        var result = Text.translatable("openmcskins.command.list");

        for (int i = 0; i < entries.length; i++) {
            result.append(Text.literal("\n").append(Text.translatable(
                    "openmcskins.command.list_entry",
                    TextUtil.formatNumber(i),
                    entries[i]
            )));
        }

        context.getSource().sendFeedback(result);

        return 0;
    }

    private int removeHost(@NotNull CommandContext<FabricClientCommandSource> context) {
        val id = context.getArgument("id", Integer.class);

        val entry = patchConfig(
                this.configInstance,
                config -> config.getHosts().remove(id.intValue())
        );

        context.getSource().sendFeedback(Text.translatable(
                "openmcskins.command.removed",
                entry.toText()
        ));

        return 0;
    }

    private int moveHost(@NotNull CommandContext<FabricClientCommandSource> context) {
        val from = context.getArgument("from", Integer.class);
        val to = context.getArgument("to", Integer.class);

        patchConfig(this.configInstance, config -> {
            val entry = config.getHosts().remove(from.intValue());
            config.getHosts().add(to, entry);

            return null;
        });

        context.getSource().sendFeedback(Text.translatable(
                "openmcskins.command.moved",
                TextUtil.formatNumber(from),
                TextUtil.formatNumber(to)
        ));

        return 0;
    }
}