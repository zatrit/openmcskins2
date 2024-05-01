package net.zatrit.skins.lib.api;

public interface Layer<T> {
    T apply(T input);
}
