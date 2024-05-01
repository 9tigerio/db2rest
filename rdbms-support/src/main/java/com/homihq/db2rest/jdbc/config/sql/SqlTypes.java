
package com.homihq.db2rest.jdbc.config.sql;


import java.sql.Types;


public class SqlTypes {
	/**
	 * A type code representing generic SQL type {@code BIT}.
	 *
	 * @see Types#BIT
	 */
	public final static int BIT = Types.BIT;

	/**
	 * A type code representing the generic SQL type {@code TINYINT}.
	 *
	 * @see Types#TINYINT
	 */
	public final static int TINYINT = Types.TINYINT;

	/**
	 * A type code representing the generic SQL type {@code SMALLINT}.
	 *
	 * @see Types#SMALLINT
	 */
	public final static int SMALLINT = Types.SMALLINT;

	/**
	 * A type code representing the generic SQL type {@code INTEGER}.
	 *
	 * @see Types#INTEGER
	 */
	public final static int INTEGER = Types.INTEGER;

	/**
	 * A type code representing the generic SQL type {@code BIGINT}.
	 *
	 * @see Types#BIGINT
	 */
	public final static int BIGINT = Types.BIGINT;

	/**
	 * A type code representing the generic SQL type {@code FLOAT}.
	 *
	 * @see Types#FLOAT
	 */
	public final static int FLOAT = Types.FLOAT;

	/**
	 * A type code representing the generic SQL type {@code REAL}.
	 *
	 * @see Types#REAL
	 */
	public final static int REAL = Types.REAL;

	/**
	 * A type code representing the generic SQL type {@code DOUBLE}.
	 *
	 * @see Types#DOUBLE
	 */
	public final static int DOUBLE = Types.DOUBLE;

	/**
	 * A type code representing the generic SQL type {@code NUMERIC}.
	 *
	 * @see Types#NUMERIC
	 */
	public final static int NUMERIC = Types.NUMERIC;

	/**
	 * A type code representing the generic SQL type {@code DECIMAL}.
	 *
	 * @see Types#DECIMAL
	 */
	public final static int DECIMAL = Types.DECIMAL;

	/**
	 * A type code representing the generic SQL type {@code CHAR}.
	 *
	 * @see Types#CHAR
	 */
	public final static int CHAR = Types.CHAR;

	/**
	 * A type code representing the generic SQL type {@code VARCHAR}.
	 *
	 * @see Types#VARCHAR
	 */
	public final static int VARCHAR = Types.VARCHAR;

	/**
	 * A type code representing the generic SQL type {@code LONGVARCHAR}.
	 * Apart from the larger default column length, this type code is treated
	 * as a synonym for {@link #VARCHAR}.
	 * @see Types#LONGVARCHAR
	 */
	public final static int LONGVARCHAR = Types.LONGVARCHAR;


	public final static int LONG32VARCHAR = 4001;


	public final static int DATE = Types.DATE;


	public final static int TIME = Types.TIME;


	public final static int TIMESTAMP = Types.TIMESTAMP;


	public final static int BINARY = Types.BINARY;


	public final static int VARBINARY = Types.VARBINARY;


	public final static int LONGVARBINARY = Types.LONGVARBINARY;


	public final static int LONG32VARBINARY = 4003;

	/**
	 * A type code representing the generic SQL value {@code NULL}.
	 *
	 * @see Types#NULL
	 */
	public final static int NULL = Types.NULL;

	/**
	 * A type code indicating that the SQL type is SQL dialect-specific
	 * and is mapped to a Java object that can be accessed via the methods
	 * {@link java.sql.ResultSet#getObject} and
	 * {@link java.sql.PreparedStatement#setObject}.
	 *
	 * @see Types#OTHER
	 */
	public final static int OTHER = Types.OTHER;


	public final static int JAVA_OBJECT = Types.JAVA_OBJECT;

	/**
	 * A type code representing the generic SQL type {@code DISTINCT}.
	 *
	 * @see Types#DISTINCT
	 */
	public final static int DISTINCT = Types.DISTINCT;

	/**
	 * A type code representing the generic SQL type {@code STRUCT}.
	 *
	 * @see Types#STRUCT
	 */
	public final static int STRUCT = Types.STRUCT;


	public final static int ARRAY = Types.ARRAY;


	public final static int TABLE = 4000;


	public final static int BLOB = Types.BLOB;


	public final static int CLOB = Types.CLOB;

	/**
	 * A type code representing the generic SQL type {@code REF}.
	 *
	 * @see Types#REF
	 */
	public final static int REF = Types.REF;

	/**
	 * A type code representing the generic SQL type {@code DATALINK}.
	 *
	 * @see Types#DATALINK
	 */
	public final static int DATALINK = Types.DATALINK;


	public final static int BOOLEAN = Types.BOOLEAN;

	/**
	 * A type code representing the generic SQL type {@code ROWID}.
	 *
	 * @see Types#ROWID
	 */
	public final static int ROWID = Types.ROWID;


	public static final int NCHAR = Types.NCHAR;


	public static final int NVARCHAR = Types.NVARCHAR;


	public static final int LONGNVARCHAR = Types.LONGNVARCHAR;


	public final static int LONG32NVARCHAR = 4002;


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

	private SqlTypes() {
	}

	/**
	 * Does the given JDBC type code represent some sort of
	 * numeric type?
	 * @param typeCode a JDBC type code from {@link Types}
	 */
	public static boolean isNumericType(int typeCode) {
		switch (typeCode) {
			case Types.BIT:
			case Types.SMALLINT:
			case Types.TINYINT:
			case Types.INTEGER:
			case Types.BIGINT:
			case Types.DOUBLE:
			case Types.REAL:
			case Types.FLOAT:
			case Types.NUMERIC:
			case Types.DECIMAL:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Is this a type with a length, that is, is it
	 * some kind of character string or binary string?
	 * @param typeCode a JDBC type code from {@link Types}
	 */
	public static boolean isStringType(int typeCode) {
		switch (typeCode) {
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.NCHAR:
			case Types.NVARCHAR:
			case Types.LONGNVARCHAR:
			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
			default:
				return false;
		}
	}

	/**
	 * Does the given JDBC type code represent some sort of
	 * character string type?
	 * @param typeCode a JDBC type code from {@link Types}
	 */
	public static boolean isCharacterOrClobType(int typeCode) {
		switch (typeCode) {
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.NCHAR:
			case Types.NVARCHAR:
			case Types.LONGNVARCHAR:
			case Types.CLOB:
			case Types.NCLOB:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Does the given JDBC type code represent some sort of
	 * character string type?
	 * @param typeCode a JDBC type code from {@link Types}
	 */
	public static boolean isCharacterType(int typeCode) {
		switch (typeCode) {
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.NCHAR:
			case Types.NVARCHAR:
			case Types.LONGNVARCHAR:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Does the given JDBC type code represent some sort of
	 * variable-length character string type?
	 * @param typeCode a JDBC type code from {@link Types}
	 */
	public static boolean isVarcharType(int typeCode) {
		switch (typeCode) {
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.NVARCHAR:
			case Types.LONGNVARCHAR:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Does the given JDBC type code represent some sort of
	 * variable-length binary string type?
	 * @param typeCode a JDBC type code from {@link Types}
	 */
	public static boolean isVarbinaryType(int typeCode) {
		switch (typeCode) {
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Does the given JDBC type code represent some sort of
	 * variable-length binary string or BLOB type?
	 * @param typeCode a JDBC type code from {@link Types}
	 */
	public static boolean isBinaryType(int typeCode) {
		switch ( typeCode ) {
			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
			case Types.BLOB:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Does the given typecode represent one of the two SQL decimal types?
	 * @param typeCode a JDBC type code from {@link Types}
	 */
	public static boolean isNumericOrDecimal(int typeCode) {
		switch ( typeCode ) {
			case NUMERIC:
			case DECIMAL:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Does the given typecode represent a SQL floating point type?
	 * @param typeCode a JDBC type code from {@link Types}
	 */
	public static boolean isFloatOrRealOrDouble(int typeCode) {
		switch ( typeCode ) {
			case FLOAT:
			case REAL:
			case DOUBLE:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Does the given typecode represent a SQL integer type?
	 * @param typeCode a JDBC type code from {@link Types}
	 */
	public static boolean isIntegral(int typeCode) {
		switch ( typeCode ) {
			case INTEGER:
			case BIGINT:
			case SMALLINT:
			case TINYINT:
				return true;
			default:
				return false;
		}
	}

	public static boolean isSmallOrTinyInt(int typeCode) {
		switch ( typeCode ) {
			case SMALLINT:
			case TINYINT:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Does the given typecode represent a SQL date, time, or timestamp type?
	 * @param typeCode a JDBC type code from {@link Types}
	 */
	public static boolean isTemporalType(int typeCode) {
		switch ( typeCode ) {
			case DATE:
			case TIME:
			case TIME_WITH_TIMEZONE:
			case TIME_UTC:
			case TIMESTAMP:
			case TIMESTAMP_WITH_TIMEZONE:
			case TIMESTAMP_UTC:
			case INSTANT:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Does the given typecode represent a SQL {@code interval} type?
	 */
	public static boolean isIntervalType(int typeCode) {
		return typeCode == INTERVAL_SECOND;
	}

	/**
	 * Does the given typecode represent a {@code duration} type?
	 */
	public static boolean isDurationType(int typeCode) {
		return typeCode == DURATION;
	}

	/**
	 * Does the given typecode represent a SQL date or timestamp type?
	 * @param typeCode a JDBC type code from {@link Types}
	 */
	public static boolean hasDatePart(int typeCode) {
		switch ( typeCode ) {
			case DATE:
			case TIMESTAMP:
			case TIMESTAMP_WITH_TIMEZONE:
			case TIMESTAMP_UTC:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Does the given typecode represent a SQL time or timestamp type?
	 * @param typeCode a JDBC type code from {@link Types}
	 */
	public static boolean hasTimePart(int typeCode) {
		switch ( typeCode ) {
			case TIME:
			case TIME_WITH_TIMEZONE:
			case TIME_UTC:
			case TIMESTAMP:
			case TIMESTAMP_WITH_TIMEZONE:
			case TIMESTAMP_UTC:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Does the typecode represent a spatial (Geometry or Geography) type.
	 *
	 * @param typeCode - a JDBC type code
	 */
	public static boolean isSpatialType(int typeCode) {
		switch ( typeCode ) {
			case GEOMETRY:
			case POINT:
			case GEOGRAPHY:
				return true;
			default:
				return false;
		}
	}

	public static boolean isEnumType(int typeCode) {
		switch ( typeCode ) {
			case ENUM:
			case NAMED_ENUM:
				return true;
			default:
				return false;
		}
	}
}
