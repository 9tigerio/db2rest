package com.homihq.db2rest.multidb;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DatabaseContextHolder {

    private final ThreadLocal<String> currentDb = new ThreadLocal<>();

    public String getCurrentDbId() {
        return currentDb.get();
    }

    public void setCurrentDbId(String tenant) {
        currentDb.set(tenant);
    }

    public void clear() {
        currentDb.remove();
    }

}
