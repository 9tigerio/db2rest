package com.homihq.db2rest.jdbc.util;

import java.util.UUID;

public class
AliasGenerator {
    private static final int UUIDLength = 36;
    private static final int UUIDNumChars = 12;


    public static String getAlias(String sqlIdentifier) {
        int LENGTH = 4;
        return
                sqlIdentifier.length() > LENGTH ?
                        sqlIdentifier.substring(0, LENGTH) + "_" + generateUUID()
                        : sqlIdentifier + "_" + generateUUID();

    }

    private static String generateUUID (){
        int startIndex = UUIDLength - UUIDNumChars;
        return UUID.randomUUID().toString().substring(startIndex,UUIDLength);
    }
}
