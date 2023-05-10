package net.zatrit.skins.lib;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.data.TextureResult;
import net.zatrit.skins.lib.util.Numbered;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static net.zatrit.skins.lib.util.SneakyLambda.function;
import static net.zatrit.skins.lib.util.SneakyLambda.sneaky;

@AllArgsConstructor
public class SkinLoader {
    private final @Getter Config SkinsConfig;

    private final Executor executor = new ScheduledThreadPoolExecutor(127);

    public void fetchAsync(
            @NotNull List<Resolver> resolvers,
            Profile profile,
            Consumer<TextureResult> success,
            Consumer<Throwable> error) {
        final var handlers = new LinkedList<Numbered<Resolver.PlayerHandler>>();

        final var futures = Numbered.enumerate(resolvers).stream()
                                    .map(pair -> CompletableFuture.supplyAsync(
                                            sneaky(() -> {
                                                final var resolver = pair.getValue();
                                                final var handler = resolver.resolve(
                                                        profile);

                                                return pair.withValue(handler);
                                            }),
                                            executor
                                    ).thenAccept(handlers::add).exceptionally(
                                            function(error)).orTimeout(
                                            5,
                                            TimeUnit.SECONDS
                                    )).toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).whenComplete((unused, throwable) -> {
            for (final var type : TextureType.values()) {
                handlers.stream().filter(pair -> pair.getValue() != null &&
                                                         pair.getValue()
                                                             .hasTexture(type))
                        .min(Comparator.comparingInt(Numbered::getIndex))
                        .ifPresent(pair -> {
                            final var handler = pair.getValue();
                            try {
                                var texture = handler.download(type);

                                success.accept(new TextureResult(texture, type));
                            } catch (IOException e) {
                                error.accept(e);
                            }
                        });
            }
        }).exceptionally(function(error));
    }
}
