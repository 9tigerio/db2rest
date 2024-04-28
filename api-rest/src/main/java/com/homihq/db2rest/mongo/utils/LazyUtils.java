package com.homihq.db2rest.mongo.utils;

import com.homihq.db2rest.mongo.rsql.structs.Lazy;

import java.util.List;

public class LazyUtils {

    private LazyUtils() {
    }

    public static <T> T firstThatReturnsNonNull(List<Lazy<T>> lazies) {
        for (Lazy<T> lazy : lazies) {
            T val = lazy.get();
            if (val != null) {
                return val;
            }
        }
        return null;
    }

}