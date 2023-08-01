package net.zatrit.skins.lib.api;

public interface Layer<T> {
    T apply(T input);

    default Layer<T> andThen(Layer<T> layer) {
        return t -> layer.apply(apply(t));
    }
}
