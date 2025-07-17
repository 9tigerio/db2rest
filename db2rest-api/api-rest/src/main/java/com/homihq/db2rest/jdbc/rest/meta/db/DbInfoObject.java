package com.homihq.db2rest.jdbc.rest.meta.db;

public record DbInfoObject(
        String dbId,
        String productName,
        int majorVersion,
        String driverName,
        String driverVersion
) {}
