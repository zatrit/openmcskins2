package net.zatrit.skins.lib;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import net.zatrit.skins.lib.util.Enumerated;
import net.zatrit.skins.lib.api.Layer;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.api.SkinLayer;
import net.zatrit.skins.lib.data.TextureResult;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static java.util.Arrays.stream;
import static net.zatrit.skins.lib.util.SneakyLambda.sneaky;

/**
 * OpenMCSkins simple loader implementation.
 */
@AllArgsConstructor
public class SkinLoader {
    private final @Getter Config config;
    private final @Getter Collection<SkinLayer> layers;

    /**
     * Asynchronously load player skins from specific resolvers.
     */
    public CompletableFuture<TextureResult[]> fetchAsync(
            @NotNull List<Resolver> resolvers, Profile profile) {
        val loaders = new LinkedList<Enumerated<Resolver.PlayerLoader>>();

        // https://stackoverflow.com/a/44521687/12245612
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        val layers = this.layers.stream().map(Layer::function)
                             .reduce(Function::andThen).get();

        /* There are more comments than the rest of the code,
         * because this is a very complex implementation. */
        val futures = Enumerated.enumerate(resolvers).stream()
                              .map(pair -> CompletableFuture.supplyAsync(
                                      /* This function may throw an exception,
                                       * but it's a CompletableFuture, so
                                       * an exception won't crash the game. */
                                      sneaky(() -> {
                                          val resolver = pair.getValue();
                                          val loader = resolver.resolve(profile);

                                          return pair.withValue(loader);
                                      }), this.config.getExecutor()).thenAccept(
                                      /* I don't know, how to pass
                                       * loaders to futures so it just,
                                       * stores them into list. */
                                      loaders::add).exceptionally(e -> null))
                              .toArray(CompletableFuture[]::new);

        val allFutures = CompletableFuture.allOf(futures);

        return allFutures.thenApply(unused -> stream(TextureType.values()).map(
                        type -> loaders.stream().parallel()
                                        // Remains only loaders that has texture
                                        .filter(pair -> pair.getValue() != null &&
                                                                pair.getValue()
                                                                        .hasTexture(type))
                                        // Find most prioritized loader and get its value
                                        .min(Comparator.comparingInt(Enumerated::getIndex))
                                        .map(sneaky(pair -> {
                                            // Convert texture into TextureResult
                                            val loader = pair.getValue();
                                            val texture = loader.download(type);

                                            return layers.apply(new TextureResult(
                                                    texture,
                                                    type
                                            ));
                                        })))
                                                      // Filter and unwrap Optionals
                                                      .filter(Optional::isPresent)
                                                      .map(Optional::get)
                                                      .toArray(TextureResult[]::new));
    }
}
