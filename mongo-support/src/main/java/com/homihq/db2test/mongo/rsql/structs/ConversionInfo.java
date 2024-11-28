package com.homihq.db2test.mongo.rsql.structs;

import com.homihq.db2test.mongo.rsql.operator.Operator;

public record ConversionInfo(
        String pathToField,
        String argument,
        Class<?> targetEntityClass,
        Operator operator
) {
}
