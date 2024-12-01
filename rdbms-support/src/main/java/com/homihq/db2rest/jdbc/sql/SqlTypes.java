package com.homihq.db2rest.jdbc.sql;


import java.sql.Types;
import java.util.Set;

import static java.sql.Types.INTEGER;


public class SqlTypes {
    /**
     * A type code representing generic SQL type {@code BIT}.
     *
     * @see Types#BIT
     */
    public static final int BIT = Types.BIT;

    /**
     * A type code representing the generic SQL type {@code TINYINT}.
     *
     * @see Types#TINYINT
     */
    public static final int TINYINT = Types.TINYINT;

    /**
     * A type code representing the generic SQL type {@code SMALLINT}.
     *
     * @see Types#SMALLINT
     */
    public static final int SMALLINT = Types.SMALLINT;

    /**
     * A type code representing the generic SQL type {@code INTEGER}.
     *
     * @see Types#INTEGER
     */
    public static final int INTEGER = Types.INTEGER;

    /**
     * A type code representing the generic SQL type {@code BIGINT}.
     *
     * @see Types#BIGINT
     */
    public static final int BIGINT = Types.BIGINT;

    /**
     * A type code representing the generic SQL type {@code FLOAT}.
     *
     * @see Types#FLOAT
     */
    public static final int FLOAT = Types.FLOAT;

    /**
     * A type code representing the generic SQL type {@code REAL}.
     *
     * @see Types#REAL
     */
    public static final int REAL = Types.REAL;

    /**
     * A type code representing the generic SQL type {@code DOUBLE}.
     *
     * @see Types#DOUBLE
     */
    public static final int DOUBLE = Types.DOUBLE;

    /**
     * A type code representing the generic SQL type {@code NUMERIC}.
     *
     * @see Types#NUMERIC
     */
    public static final int NUMERIC = Types.NUMERIC;

    /**
     * A type code representing the generic SQL type {@code DECIMAL}.
     *
     * @see Types#DECIMAL
     */
    public static final int DECIMAL = Types.DECIMAL;

    /**
     * A type code representing the generic SQL type {@code CHAR}.
     *
     * @see Types#CHAR
     */
    public static final int CHAR = Types.CHAR;

    /**
     * A type code representing the generic SQL type {@code VARCHAR}.
     *
     * @see Types#VARCHAR
     */
    public static final int VARCHAR = Types.VARCHAR;

    /**
     * A type code representing the generic SQL type {@code LONGVARCHAR}.
     * Apart from the larger default column length, this type code is treated
     * as a synonym for {@link #VARCHAR}.
     *
     * @see Types#LONGVARCHAR
     */
    public static final int LONGVARCHAR = Types.LONGVARCHAR;


    public static final int LONG32VARCHAR = 4001;


    public static final int DATE = Types.DATE;


    public static final int TIME = Types.TIME;


    public static final int TIMESTAMP = Types.TIMESTAMP;


    public static final int BINARY = Types.BINARY;


    public static final int VARBINARY = Types.VARBINARY;


    public static final int LONGVARBINARY = Types.LONGVARBINARY;


    public static final int LONG32VARBINARY = 4003;

    /**
     * A type code representing the generic SQL value {@code NULL}.
     *
     * @see Types#NULL
     */
    public static final int NULL = Types.NULL;

    /**
     * A type code indicating that the SQL type is SQL dialect-specific
     * and is mapped to a Java object that can be accessed via the methods
     * {@link java.sql.ResultSet#getObject} and
     * {@link java.sql.PreparedStatement#setObject}.
     *
     * @see Types#OTHER
     */
    public static final int OTHER = Types.OTHER;


    public static final int JAVA_OBJECT = Types.JAVA_OBJECT;

    /**
     * A type code representing the generic SQL type {@code DISTINCT}.
     *
     * @see Types#DISTINCT
     */
    public static final int DISTINCT = Types.DISTINCT;

    /**
     * A type code representing the generic SQL type {@code STRUCT}.
     *
     * @see Types#STRUCT
     */
    public static final int STRUCT = Types.STRUCT;


    public static final int ARRAY = Types.ARRAY;


    public static final int TABLE = 4000;


    public static final int BLOB = Types.BLOB;


    public static final int CLOB = Types.CLOB;

    /**
     * A type code representing the generic SQL type {@code REF}.
     *
     * @see Types#REF
     */
    public static final int REF = Types.REF;

    /**
     * A type code representing the generic SQL type {@code DATALINK}.
     *
     * @see Types#DATALINK
     */
    public static final int DATALINK = Types.DATALINK;


    public static final int BOOLEAN = Types.BOOLEAN;

    /**
     * A type code representing the generic SQL type {@code ROWID}.
     *
     * @see Types#ROWID
     */
    public static final int ROWID = Types.ROWID;


    public static final int NCHAR = Types.NCHAR;


    public static final int NVARCHAR = Types.NVARCHAR;


    public static final int LONGNVARCHAR = Types.LONGNVARCHAR;


    public static final int LONG32NVARCHAR = 4002;


    public static final int NCLOB = Types.NCLOB;


    public static final int SQLXML = Types.SQLXML;

    /**
     * A type code representing the generic SQL type {@code REF CURSOR}.
     *
     * @see Types#REF_CURSOR
     */
    public static final int REF_CURSOR = Types.REF_CURSOR;

    /**
     * A type code representing identifies the generic SQL type
     * {@code TIME WITH TIMEZONE}.
     *
     * @see Types#TIME_WITH_TIMEZONE
     */
    public static final int TIME_WITH_TIMEZONE = Types.TIME_WITH_TIMEZONE;


    public static final int TIMESTAMP_WITH_TIMEZONE = Types.TIMESTAMP_WITH_TIMEZONE;

    // Misc types


    public static final int UUID = 3000;


    public static final int JSON = 3001;


    public static final int INET = 3002;


    public static final int TIMESTAMP_UTC = 3003;


    public static final int MATERIALIZED_BLOB = 3004;


    public static final int MATERIALIZED_CLOB = 3005;


    public static final int MATERIALIZED_NCLOB = 3006;


    public static final int TIME_UTC = 3007;


    // Java Time (java.time) "virtual" JdbcTypes


    public static final int INSTANT = 3008;


    public static final int LOCAL_DATE_TIME = 3009;


    public static final int LOCAL_DATE = 3010;


    public static final int LOCAL_TIME = 3011;


    public static final int OFFSET_DATE_TIME = 3012;


    public static final int OFFSET_TIME = 3013;


    public static final int ZONED_DATE_TIME = 3014;


    public static final int DURATION = 3015;

    // Interval types


    public static final int INTERVAL_SECOND = 3100;

    // Geometry types

    /**
     * A type code representing the generic SQL type {@code GEOMETRY}.
     */
    public static final int GEOMETRY = 3200;

    /**
     * A type code representing the generic SQL type {@code POINT}.
     */
    public static final int POINT = 3201;

    /**
     * A type code representing the generic SQL type {@code GEOGRAPHY}.
     *
     * @since 6.0.1
     */
    public static final int GEOGRAPHY = 3250;


    public static final int ENUM = 6000;


    public static final int NAMED_ENUM = 6001;

    public static final int VECTOR = 10_000;

    private static final Set<Integer> NUMERIC_TYPES = Set.of(
            Types.BIT,
            Types.SMALLINT,
            Types.TINYINT,
            Types.INTEGER,
            Types.BIGINT,
            Types.DOUBLE,
            Types.REAL,
            Types.FLOAT,
            Types.NUMERIC,
            Types.DECIMAL
    );

    private static final Set<Integer> STRING_TYPES = Set.of(
            Types.CHAR,
            Types.VARCHAR,
            Types.LONGVARCHAR,
            Types.NCHAR,
            Types.NVARCHAR,
            Types.LONGNVARCHAR,
            Types.BINARY,
            Types.VARBINARY,
            Types.LONGVARBINARY
    );

    private static final Set<Integer> CHAR_TYPES = Set.of(
            Types.CHAR,
            Types.VARCHAR,
            Types.LONGVARCHAR,
            Types.NCHAR,
            Types.NVARCHAR,
            Types.LONGNVARCHAR
    );

    private static final Set<Integer> VARCHAR_TYPES = Set.of(
            Types.CHAR,
            Types.VARCHAR,
            Types.LONGVARCHAR,
            Types.NCHAR,
            Types.NVARCHAR,
            Types.LONGNVARCHAR
    );

    private static final Set<Integer> VARBINARY_TYPES = Set.of(
            Types.VARBINARY,
            Types.LONGVARBINARY
    );

    private static final Set<Integer> BINARY_TYPES = Set.of(
            Types.BINARY,
            Types.VARBINARY,
            Types.LONGVARBINARY,
            Types.BLOB
    );

    private static final Set<Integer> CLOB_TYPES = Set.of(
            Types.CLOB,
            Types.NCLOB
    );

    private static final Set<Integer> FLOATING_POINT_TYPES =  Set.of(FLOAT, REAL, DOUBLE);

    private static final Set<Integer> INTEGER_TYPES = Set.of(
            Types.INTEGER,
            Types.BIGINT,
            Types.SMALLINT,
            Types.TINYINT
    );

    private static final Set<Integer> TEMPORAL_TYPES = Set.of(
            Types.DATE,
            Types.TIME,
            Types.TIME_WITH_TIMEZONE,
            TIME_UTC,
            Types.TIMESTAMP,
            Types.TIMESTAMP_WITH_TIMEZONE,
            TIMESTAMP_UTC,
            INSTANT
    );

    private static final Set<Integer> DATE_TYPES = Set.of(DATE);

    private static final Set<Integer> TIMESTAMP_TYPES = Set.of(
            TIME,
            TIME_WITH_TIMEZONE,
            TIME_UTC,
            TIMESTAMP,
            TIMESTAMP_WITH_TIMEZONE,
            TIMESTAMP_UTC
    );

    private static final Set<Integer> ENUM_TYPES = Set.of(
            ENUM,
            NAMED_ENUM
    );

    private static final Set<Integer> SPATIAL_TYPES = Set.of(
            GEOMETRY,
            POINT,
            GEOGRAPHY
    );

    /**
     * Does the given JDBC type code represent some sort of
     * numeric type?
     *
     * @param typeCode a JDBC type code from {@link Types}
     */
    public boolean isNumericType(int typeCode) {
        return NUMERIC_TYPES.contains(typeCode);
    }

    /**
     * Is this a type with a length, that is, is it
     * some kind of character string or binary string?
     *
     * @param typeCode a JDBC type code from {@link Types}
     */
    public boolean isStringType(int typeCode) {
        return STRING_TYPES.contains(typeCode);
    }

    /**
     * Does the given JDBC type code represent some sort of
     * character string type?
     *
     * @param typeCode a JDBC type code from {@link Types}
     */
    public boolean isCharacterOrClobType(int typeCode) {
        return isCharacterType(typeCode) || CLOB_TYPES.contains(typeCode);
    }

    /**
     * Does the given JDBC type code represent some sort of
     * character string type?
     *
     * @param typeCode a JDBC type code from {@link Types}
     */
    public boolean isCharacterType(int typeCode) {
        return CHAR_TYPES.contains(typeCode);
    }

    /**
     * Does the given JDBC type code represent some sort of
     * variable-length character string type?
     *
     * @param typeCode a JDBC type code from {@link Types}
     */
    public boolean isVarcharType(int typeCode) {
        return VARCHAR_TYPES.contains(typeCode);
    }

    /**
     * Does the given JDBC type code represent some sort of
     * variable-length binary string type?
     *
     * @param typeCode a JDBC type code from {@link Types}
     */
    public boolean isVarbinaryType(int typeCode) {
        return VARBINARY_TYPES.contains(typeCode);
    }

    /**
     * Does the given JDBC type code represent some sort of
     * variable-length binary string or BLOB type?
     *
     * @param typeCode a JDBC type code from {@link Types}
     */
    public boolean isBinaryType(int typeCode) {
        return BINARY_TYPES.contains(typeCode);
    }

    /**
     * Does the given typecode represent one of the two SQL decimal types?
     *
     * @param typeCode a JDBC type code from {@link Types}
     */
    public boolean isNumericOrDecimal(int typeCode) {
        return Set.of(NUMERIC, DECIMAL).contains(typeCode);
    }

    /**
     * Does the given typecode represent a SQL floating point type?
     *
     * @param typeCode a JDBC type code from {@link Types}
     */
    public boolean isFloatOrRealOrDouble(int typeCode) {
        return FLOATING_POINT_TYPES.contains(typeCode);
    }

    /**
     * Does the given typecode represent a SQL integer type?
     *
     * @param typeCode a JDBC type code from {@link Types}
     */
    public boolean isInteger(int typeCode) {
        return INTEGER_TYPES.contains(typeCode);
    }

    public boolean isSmallOrTinyInt(int typeCode) {
        return Set.of(SMALLINT, TINYINT).contains(typeCode);
    }

    /**
     * Does the given typecode represent a SQL date, time, or timestamp type?
     *
     * @param typeCode a JDBC type code from {@link Types}
     */
    public boolean isTemporalType(int typeCode) {
        return TEMPORAL_TYPES.contains(typeCode);
    }

    /**
     * Does the given typecode represent a SQL {@code interval} type?
     */
    public boolean isIntervalType(int typeCode) {
        return typeCode == INTERVAL_SECOND;
    }

    /**
     * Does the given typecode represent a {@code duration} type?
     */
    public boolean isDurationType(int typeCode) {
        return typeCode == DURATION;
    }

    /**
     * Does the given typecode represent a SQL date or timestamp type?
     *
     * @param typeCode a JDBC type code from {@link Types}
     */
    public boolean hasDatePart(int typeCode) {
        return DATE_TYPES.contains(typeCode) || TIMESTAMP_TYPES.contains(typeCode);
    }

    /**
     * Does the given typecode represent a SQL time or timestamp type?
     *
     * @param typeCode a JDBC type code from {@link Types}
     */
    public boolean hasTimePart(int typeCode) {
        return TIMESTAMP_TYPES.contains(typeCode);
    }

    /**
     * Does the typecode represent a spatial (Geometry or Geography) type.
     *
     * @param typeCode - a JDBC type code
     */
    public boolean isSpatialType(int typeCode) {
        return SPATIAL_TYPES.contains(typeCode);
    }

    public boolean isEnumType(int typeCode) {
        return ENUM_TYPES.contains(typeCode);
    }

}
