package com.homihq.db2rest.schema;


import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AliasGenerator {
    private final Random random = new Random();

    public String getAlias(String sqlIdentifier) {
        int LENGTH = 4;
        return
        sqlIdentifier.length() > LENGTH ?
                sqlIdentifier.substring(0, LENGTH) + "_" +random.nextInt(100)
            :   sqlIdentifier + "_" +random.nextInt(100);

    }
}
