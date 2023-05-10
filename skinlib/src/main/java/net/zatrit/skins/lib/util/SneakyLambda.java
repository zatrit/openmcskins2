package net.zatrit.skins.lib.util;

import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SneakyLambda {
    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull Supplier<T> sneaky(ThrowableSupplier<T> supplier) {
        return new Supplier<T>() {
            @Override
            @SneakyThrows
            public T get() {
                return supplier.get();
            }
        };
    }


    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull Consumer<T> sneaky(ThrowableConsumer<T> consumer) {
        return new Consumer<T>() {
            @Override
            @SneakyThrows
            public void accept(T t) {
                consumer.accept(t);
            }
        };
    }

    @Contract(pure = true)
    public static <T> @NotNull Function<T, Void> function(Consumer<T> consumer) {
        return v -> {
            consumer.accept(v);
            return null;
        };
    }

    public interface ThrowableSupplier<T> {
        T get() throws Throwable;
    }

    public interface ThrowableConsumer<T> {
        void accept(T t) throws Throwable;
    }
}
