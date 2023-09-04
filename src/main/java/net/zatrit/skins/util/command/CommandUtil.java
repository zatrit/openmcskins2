package net.zatrit.skins.util.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import lombok.experimental.UtilityClass;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class CommandUtil {
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull LiteralArgumentBuilder<ServerCommandSource> literal(
            final String name) {
        return LiteralArgumentBuilder.<ServerCommandSource>literal(name)
                       .executes(CommandUtil::noArguments);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull <T> RequiredArgumentBuilder<ServerCommandSource, T> argument(
            final String name, final ArgumentType<T> type) {
        return RequiredArgumentBuilder.<ServerCommandSource, T>argument(
                name,
                type
        ).executes(CommandUtil::noArguments);
    }

    public static int noArguments(@NotNull CommandContext<ServerCommandSource> context) {
        context.getSource().sendError(new TranslatableText(
                "command.unknown.argument"));
        return -1;
    }
}
