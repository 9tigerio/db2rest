package com.homihq.db2rest.jdbc.multidb;

public abstract class DatabaseContextHolder {


    private static final ThreadLocal<String> currentDb = new ThreadLocal<String>();

    public static void setCurrentDbId(String tenant) {
        currentDb.set(tenant);
    }

    public static String getCurrentDbId() {
        return currentDb.get();
    }

    public static void clear() {
        currentDb.remove();
    }

}
