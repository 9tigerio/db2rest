package com.homihq.db2rest.type;

import org.joda.convert.StringConvert;

public class JdbcDataTypeConverter {

    private JdbcDataTypeConverter() {}

    public static Object convert(String value, Class<?> type) {
        if(type.isAssignableFrom(Integer.class)) {
            return StringConvert.INSTANCE.convertFromString(Integer.class, value);
        }
        else{
            return value;
        }
    }
}
