package com.db2rest.jdbc.dialect.model;



public record ArrayTypeValueHolder(String jdbcType, String sqlType, Object[] values) {
}
