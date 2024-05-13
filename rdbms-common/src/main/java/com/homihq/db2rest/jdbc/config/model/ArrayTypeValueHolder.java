package com.homihq.db2rest.jdbc.config.model;



public record ArrayTypeValueHolder(String jdbcType, String sqlType, Object [] values) {
}
