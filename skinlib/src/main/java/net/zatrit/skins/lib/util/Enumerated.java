package net.zatrit.skins.lib.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class Enumerated<T> {
    private final int index;
    private final T value;

    /**
     * @return new list of numbered with values from input list.
     */
    public static <T> @NotNull Collection<Enumerated<T>> enumerate(@NotNull List<T> list) {
        /* Predetermines the capacity of the list in advance
         * so as not to allocate memory during addition. */
        List<Enumerated<T>> list2 = new ArrayList<>(list.size());

        for (int i = 0; i < list.size(); i++) {
            list2.add(new Enumerated<>(i, list.get(i)));
        }

        return list2;
    }

    /**
     * @return new numbered instance with same index, but different value.
     */
    public <R> Enumerated<R> withValue(R newValue) {
        return new Enumerated<>(this.getIndex(), newValue);
    }
}
