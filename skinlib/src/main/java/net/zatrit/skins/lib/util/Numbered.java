package net.zatrit.skins.lib.util;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public @Data class Numbered<T> {
    private final int index;
    private final T value;

    public static <T> @NotNull Collection<Numbered<T>> enumerate(@NotNull List<T> list) {
        List<Numbered<T>> list2 = new LinkedList<>();

        for (int i = 0; i < list.size(); i++) {
            list2.add(new Numbered<>(i, list.get(i)));
        }

        return list2;
    }

    public <R> Numbered<R> withValue(R newValue) {
        return new Numbered<>(this.getIndex(), newValue);
    }
}
