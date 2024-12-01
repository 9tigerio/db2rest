package com.homihq.db2rest.jdbc.util;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class AliasGenerator {

    private static final int MAX_ALIAS_LENGTH = 4;

    private static final Random random = new Random();

    public String getAlias(String sqlIdentifier) {
        return sqlIdentifier.length() > MAX_ALIAS_LENGTH
                ? sqlIdentifier.substring(0, MAX_ALIAS_LENGTH) + "_" + random.nextInt(100)
                : sqlIdentifier + "_" + random.nextInt(1000);

    }
}
