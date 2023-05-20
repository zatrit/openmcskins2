package net.zatrit.skins.lib.util;

import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Similar to {@link lombok.SneakyThrows} wrapper for lambdas.
 */
public class SneakyLambda {
    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull Supplier<T> sneaky(SupplierThrows<T> supplier) {
        return new Supplier<>() {
            @Override
            @SneakyThrows
            public T get() {
                return supplier.get();
            }
        };
    }


    @Contract(value = "_ -> new", pure = true)
    public static <T, R> @NotNull Function<T, R> sneaky(FunctionThrows<T, R> function) {
        return new Function<>() {
            @Override
            @SneakyThrows
            public R apply(T t) {
                return function.apply(t);
            }
        };
    }

    @Contract(value = "_ -> new", pure = true)
    public static <T, U> @NotNull BiConsumer<T, U> sneaky(
            BiConsumerThrows<T, U> consumer) {
        return new BiConsumer<>() {
            @Override
            @SneakyThrows
            public void accept(T t, U u) {
                consumer.accept(t, u);
            }
        };
    }

    public interface SupplierThrows<T> {
        T get() throws Throwable;
    }

    public interface FunctionThrows<T, R> {
        R apply(T t) throws Throwable;
    }

    public interface BiConsumerThrows<T, U> {
        void accept(T t, U u) throws Throwable;
    }
}
