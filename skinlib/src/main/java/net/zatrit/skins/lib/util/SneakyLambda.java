package net.zatrit.skins.lib.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Similar to {@link lombok.SneakyThrows} wrapper for lambdas.
 */
@UtilityClass
public class SneakyLambda {
    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull Supplier<T> sneaky(Callable<T> supplier) {
        return new Supplier<T>() {
            @Override
            @SneakyThrows
            public T get() {
                return supplier.call();
            }
        };
    }

    @Contract(value = "_ -> new", pure = true)
    public static <T, R> @NotNull Function<T, R> sneaky(
            FunctionThrows<T, R> function) {
        return new Function<T, R>() {
            @Override
            @SneakyThrows
            public R apply(T t) {
                return function.apply(t);
            }
        };
    }

    @FunctionalInterface
    public interface FunctionThrows<T, R> {
        R apply(T t) throws Throwable;
    }
}
