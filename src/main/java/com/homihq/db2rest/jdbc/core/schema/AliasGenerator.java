package com.homihq.db2rest.jdbc.core.schema;



import java.util.Random;


public class AliasGenerator {
    private static final Random random = new Random();

    public static String getAlias(String sqlIdentifier) {
        int LENGTH = 4;
        return
        sqlIdentifier.length() > LENGTH ?
                sqlIdentifier.substring(0, LENGTH) + "_" +random.nextInt(100)
            :   sqlIdentifier + "_" +random.nextInt(1000);

    }
}
