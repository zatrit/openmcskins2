package net.zatrit.skins.util;

import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
public interface ExceptionConsumer<R>
        extends Consumer<Throwable>, Function<Throwable, R> {
    default <T> ExceptionConsumer<T> andReturn(T value) {
        return error -> {
            this.accept(error);
            return value;
        };
    }

    @Override
    default void accept(Throwable error) {
        this.apply(error);
    }
}
