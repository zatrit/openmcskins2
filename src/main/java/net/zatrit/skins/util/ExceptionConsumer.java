package net.zatrit.skins.util;

import java.util.function.Consumer;
import java.util.function.Function;

public interface ExceptionConsumer<R> extends Consumer<Throwable>, Function<Throwable, R> {
    default <T> ExceptionConsumer<T> andReturn(T value) {
        final var parent = this;
        return new ExceptionConsumer<>() {
            @Override
            public void accept(Throwable error) {
                parent.accept(error);
            }

            @Override
            public T apply(Throwable error) {
                parent.accept(error);
                return value;
            }
        };
    }
}
