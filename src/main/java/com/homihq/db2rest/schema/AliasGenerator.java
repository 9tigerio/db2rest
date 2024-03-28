package com.homihq.db2rest.schema;



import java.util.Random;


public class AliasGenerator {
    private static Random random = new Random();

    public static String getAlias(String sqlIdentifier) {
        int LENGTH = 4;
        return
        sqlIdentifier.length() > LENGTH ?
                sqlIdentifier.substring(0, LENGTH) + "_" +random.nextInt(100)
            :   sqlIdentifier + "_" +random.nextInt(100);

    }
}
