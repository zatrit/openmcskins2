package net.zatrit.skins.lib.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Index-value pair.
 */
@Getter
@AllArgsConstructor
public class Enumerated<T> {
    private final int index;
    private final T value;

    /**
     * @return stream of numbered with values from input list.
     */
    @ApiStatus.Internal
    public static <T> @NotNull Stream<Enumerated<T>> enumerate(
        @NotNull List<T> list) {
        return IntStream.range(0, list.size())
            .mapToObj(index -> new Enumerated<>(index, list.get(index)));
    }

    /**
     * @return new numbered instance with same index, but different value.
     */
    @ApiStatus.Internal
    public <R> Enumerated<R> withValue(R newValue) {
        return new Enumerated<>(this.getIndex(), newValue);
    }
}
