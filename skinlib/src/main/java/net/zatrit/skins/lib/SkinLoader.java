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

/**
 * OpenMCSkins simple loader implementation.
 */
@AllArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class SkinLoader {
    private final @Getter Config config;

    /**
     * Asynchronously load player skins from specific resolvers.
     */
    public CompletableFuture<TextureResult[]> fetchAsync(
            @NotNull List<Resolver> resolvers, Profile profile) {
        final var loaders = new LinkedList<Numbered<Resolver.PlayerLoader>>();
        final var timeout = getConfig().getLoaderTimeout();

        /* There are more comments than the rest of the code,
         * because this is a very complex implementation. */
        final var futures = Numbered.enumerate(resolvers)
                                    .stream()
                                    .map(pair -> CompletableFuture.supplyAsync(
                                                    /* This function may throw an exception,
                                                     * but it's a CompletableFuture, so
                                                     * an exception won't crash the game. */
                                                    sneaky(() -> {
                                                        final var resolver = pair.getValue();
                                                        final var loader = resolver.resolve(
                                                                profile);

                                                        return pair.withValue(loader);
                                                    }), this.config.getExecutor())
                                                         .thenAccept(
                                                                 /* I don't know, how to pass
                                                                  * loaders to futures so it just,
                                                                  * stores them into list. */
                                                                 loaders::add)
                                                         .orTimeout(
                                                                 timeout,
                                                                 TimeUnit.SECONDS
                                                         )
                                                         .exceptionally(e -> null))
                                    .toArray(CompletableFuture[]::new);

        final var allFutures = CompletableFuture.allOf(futures);
        return allFutures.thenApply(unused -> stream(TextureType.values()).map(
                        type -> loaders.stream()
                                        .parallel()
                                        // Remains only loaders that has texture
                                        .filter(pair -> pair.getValue() != null &&
                                                                pair.getValue()
                                                                        .hasTexture(type))
                                        // Find most prioritized loader and get its value
                                        .min(Comparator.comparingInt(Numbered::getIndex))
                                        .map(sneaky(pair -> {
                                            // Convert texture into TextureResult
                                            final var loader = pair.getValue();
                                            final var texture = loader.download(type);

                                            return new TextureResult(texture, type);
                                        })))
                                                      // Filter and unwrap Optionals
                                                      .filter(Optional::isPresent)
                                                      .map(Optional::get)
                                                      .toArray(TextureResult[]::new));
    }
}
