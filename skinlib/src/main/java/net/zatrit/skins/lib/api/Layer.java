package net.zatrit.skins.lib.api;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public interface Layer<T> extends UnaryOperator<T> {
    default Function<T, T> function() {
        return this;
    }
}
