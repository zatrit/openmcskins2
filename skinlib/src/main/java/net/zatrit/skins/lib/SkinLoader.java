package net.zatrit.skins.lib;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.data.TextureResult;
import net.zatrit.skins.lib.util.Numbered;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.stream;
import static net.zatrit.skins.lib.util.SneakyLambda.sneaky;

@AllArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class SkinLoader {
    private final @Getter Config skinsConfig;

    public CompletableFuture<TextureResult[]> fetchAsync(
            @NotNull List<Resolver> resolvers, Profile profile) {
        final var handlers = new LinkedList<Numbered<Resolver.PlayerHandler>>();

        final var futures = Numbered.enumerate(resolvers).stream()
                                    .map(pair -> CompletableFuture.supplyAsync(
                                            sneaky(() -> {
                                                final var resolver = pair.getValue();
                                                final var handler = resolver.resolve(
                                                        profile);

                                                return pair.withValue(handler);
                                            }),
                                            this.skinsConfig.getExecutor()
                                    ).thenAccept(handlers::add).orTimeout(
                                            5,
                                            TimeUnit.SECONDS
                                    )).toArray(CompletableFuture[]::new);

        final var allFutures = CompletableFuture.allOf(futures);
        return allFutures.thenApply(unused -> stream(TextureType.values()).map(
                        type -> handlers.stream().parallel()
                                        .filter(pair -> pair.getValue() != null &&
                                                                pair.getValue()
                                                                        .hasTexture(type))
                                        .min(Comparator.comparingInt(Numbered::getIndex))
                                        .map(sneaky(pair -> {
                                            final var handler = pair.getValue();

                                            var texture = handler.download(type);

                                            return new TextureResult(texture, type);
                                        }))).filter(Optional::isPresent)
                                                      .map(Optional::get)
                                                      .toArray(
                                                              TextureResult[]::new));
    }
}
