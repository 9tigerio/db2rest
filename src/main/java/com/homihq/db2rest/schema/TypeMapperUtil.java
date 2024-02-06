package com.homihq.db2rest.schema;

import com.homihq.db2rest.exception.InvalidColumnException;
import org.apache.commons.lang3.StringUtils;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

import java.sql.Types;
import java.sql.JDBCType;

public class TypeMapperUtil {

    private TypeMapperUtil() {}


    public static Object parseValue(String value, Class<?> type) {

        if (String.class == type) {
            return value;
        }
        else if (Boolean.class == type || boolean.class == type) {
            return Boolean.valueOf(value);
        }
        else if (Integer.class == type || int.class == type) {
            return Integer.valueOf(value);
        }

        else {
            return value;
        }

    }

    public static Class<?> getColumnJavaType(Table table, String columnName) {
        return table.getColumns()
                .stream()
                .filter(c -> StringUtils.equalsIgnoreCase(c.getName(), columnName))
                .findFirst().orElseThrow(() -> new InvalidColumnException(table.getName(), columnName))
                .getColumnDataType()
                .getJavaSqlType().getDefaultMappedClass();
    }

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
