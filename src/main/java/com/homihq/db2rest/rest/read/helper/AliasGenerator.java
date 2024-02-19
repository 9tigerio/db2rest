package com.homihq.db2rest.rest.read.helper;


import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AliasGenerator {
    private final Random random = new Random();
    public String getAlias(String prefix, int length, String sqlIdentifier) {
        return
        sqlIdentifier.length() > length ?
                prefix + sqlIdentifier.substring(0,length) + "_" +random.nextInt(2000)
            :  prefix + sqlIdentifier + "_" +random.nextInt(2000);

    }
}
