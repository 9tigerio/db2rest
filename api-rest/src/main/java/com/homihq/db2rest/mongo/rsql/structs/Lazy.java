package com.homihq.db2rest.mongo.rsql.structs;

import java.util.function.Supplier;

/**
 * A lazy supplier that only executes when retrieved and then continues to
 * return the same value.
 *
 * @param <T>
 */
public class Lazy<T> implements Supplier<T> {

    public static <S> Lazy<S> empty() {
        Lazy<S> lazy = new Lazy<>();
        lazy.hasRun = true;
        return lazy;
    }

    public static <S> Lazy<S> fromValue(S value) {
        Lazy<S> lazy = new Lazy<>();
        lazy.value = value;
        lazy.hasRun = true;
        return lazy;
    }

    public static <S> Lazy<S> fromFunc(Supplier<S> supplier) {
        Lazy<S> lazy = new Lazy<>();
        lazy.supplier = supplier;
        return lazy;
    }

    private Lazy() {
    }

    private T value;
    private boolean hasRun;
    private Supplier<T> supplier;

    @Override
    public T get() {
        if (!hasRun) {
            this.value = supplier.get();
            hasRun = true;
            supplier = null;
        }
        return this.value;
    }

}
