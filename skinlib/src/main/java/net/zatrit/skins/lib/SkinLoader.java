package net.zatrit.skins.lib;

import net.zatrit.skins.lib.data.TextureResult;
import net.zatrit.skins.lib.resolver.Resolver;
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

public class SkinLoader {
    private final Executor executor = new ScheduledThreadPoolExecutor(127);

    public void fetchAsync(@NotNull List<Resolver> resolvers, Profile profile,
            Consumer<TextureResult> success, Consumer<Throwable> error) {
        final var handlers = new LinkedList<Numbered<Resolver.PlayerHandler>>();

        var futures = Numbered.enumerate(resolvers).stream()
                .map(pair -> CompletableFuture.supplyAsync(() -> {
                    final var resolver = pair.getValue();
                    Resolver.PlayerHandler handler = null;
                    try {
                        handler = resolver.resolve(profile);
                    } catch (IOException e) {
                        error.accept(e);
                    }
                    return pair.withValue(handler);
                }, executor).thenAccept(handlers::add).orTimeout(5, TimeUnit.SECONDS))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).whenComplete((unused, throwable) -> {
            for (var type : TextureType.values()) {
                handlers.stream()
                        .filter(pair -> pair.getValue() != null && pair.getValue().hasTexture(type))
                        .min(Comparator.comparingInt(Numbered::getIndex)).ifPresent(pair -> {
                            final var handler = pair.getValue();
                            try {
                                var texture = handler.download(type);

                                success.accept(new TextureResult(texture, type));
                            } catch (IOException e) {
                                error.accept(e);
                            }
                        });
            }
        });
    }
}
