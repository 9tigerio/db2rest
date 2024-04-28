package com.homihq.db2rest.mongo.rsql.structs;

import com.homihq.db2rest.mongo.rsql.operator.Operator;

public record ConversionInfo(String pathToField, String argument, Class<?> targetEntityClass, Operator operator) {
}
