package net.zatrit.skins.lib;

import lombok.AllArgsConstructor;
import lombok.val;
import net.zatrit.skins.lib.api.PlayerLoader;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.data.TypedTexture;
import net.zatrit.skins.lib.util.Enumerated;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static net.zatrit.skins.lib.util.SneakyLambda.sneaky;

@AllArgsConstructor
public class TextureDispatcher {
    private final Config config;

    /*
     * There are more comments than the rest of the code,
     * because this is a very complex implementation.
     */

    /**
     * Asynchronously fetches loaders and numbers them from specific resolvers.
     */
    public Stream<CompletableFuture<Enumerated<PlayerLoader>>> resolveAsync(
            @NotNull List<Resolver> resolvers, Profile profile) {
        return Enumerated.enumerate(resolvers).stream()
                .map(pair -> CompletableFuture.supplyAsync(
                        /*
                         * This function may throw an exception,
                         * but it's a CompletableFuture, so
                         * an exception won't crash the game.
                         */
                        sneaky(() -> {
                            val resolver = pair.getValue();
                            val loader = resolver.resolve(profile);

                            return pair.withValue(loader);
                        }), this.config.getExecutor()));
    }

    /**
     * Asynchronously fetches textures from a list of numbered futures returning loaders.
     * <p>
     * Use {@link #resolveAsync} to obtain futures list.
     */
    public CompletableFuture<TypedTexture[]> fetchTexturesAsync(
            @NotNull Stream<CompletableFuture<Enumerated<PlayerLoader>>> loaderFutures) {
        val loaders = new LinkedList<Enumerated<PlayerLoader>>();

        val futures = loaderFutures
                // Add the loader to the list and
                // do nothing if unsuccessful
                .map(l -> l.thenAccept(loaders::add).exceptionally(e -> null))
                .toArray(CompletableFuture[]::new);
        val allFutures = CompletableFuture.allOf(futures);

        return allFutures.thenApply(unused -> stream(TextureType.values()).map(
                        type -> loaders.stream().parallel()
                                // Remains only loaders that has texture
                                .filter(Objects::nonNull)
                                .filter(pair -> pair.getValue() != null &&
                                        pair.getValue().hasTexture(type))
                                // Find most prioritized loader and get its value
                                .min(Comparator.comparingInt(Enumerated::getIndex))
                                .map(pair -> {
                                    // Convert texture into TextureResult
                                    val loader = pair.getValue();
                                    return loader.getTexture(type);
                                }))
                // Filter and unwrap Optionals
                .filter(Optional::isPresent).map(Optional::get)
                .toArray(TypedTexture[]::new));
    }
}
