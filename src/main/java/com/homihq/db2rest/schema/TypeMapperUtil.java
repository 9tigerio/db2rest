package com.homihq.db2rest.schema;

import schemacrawler.schema.Column;

import java.sql.Types;
import java.sql.JDBCType;

public class TypeMapperUtil {

    private TypeMapperUtil() {}

    public static JDBCType getJdbcType(Column column) {
        switch (column.getColumnDataType().getJavaSqlType().getVendorTypeNumber()) {
            case Types.VARCHAR, 1111 -> {
                return JDBCType.VARCHAR;
            }
            case Types.INTEGER, 2001 -> {
                return JDBCType.INTEGER;
            }
            case Types.BIGINT -> {
                return JDBCType.BIGINT;
            }
            case Types.CHAR -> {
                return JDBCType.CHAR;
            }
            case Types.DATE -> {
                return JDBCType.DATE;
            }
            case Types.TIMESTAMP -> {
                return JDBCType.TIMESTAMP;
            }
            case Types.DECIMAL, Types.NUMERIC -> {
                return JDBCType.DECIMAL;
            }
            case Types.SMALLINT, Types.BIT -> {
                return JDBCType.SMALLINT;
            }
            case Types.BLOB, Types.BINARY -> {
                return JDBCType.BLOB;
            }
            case Types.CLOB -> {
                return JDBCType.CLOB;
            }
            case 2147483647 -> {
                JDBCType jt = getJdbcTypeForUnknownJavaSqlType(column);
                if (jt != null) {
                    return jt;
                }
            }
        }
        throw new IllegalArgumentException("Unsupported column type " + column.getColumnDataType().getJavaSqlType() + " (" + column.getColumnDataType().getJavaSqlType() + ")");
    }

    protected static JDBCType getJdbcTypeForUnknownJavaSqlType(Column column) {
        /*
         * unknown type
         */
        if (column.getColumnDataType().getName().contains("TIMESTAMP")) {
            return JDBCType.TIMESTAMP;
        }
        return null;
    }
}
