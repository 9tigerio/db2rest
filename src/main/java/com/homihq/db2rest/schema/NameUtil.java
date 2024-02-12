package com.homihq.db2rest.schema;

@Deprecated
public abstract class NameUtil {

    public static String getAlias(int i, String prefix) {
        String alphabets = "abcdefghijklmnopqrstuvwxyz";

        return prefix + alphabets.charAt(i) + i;
    }
}
