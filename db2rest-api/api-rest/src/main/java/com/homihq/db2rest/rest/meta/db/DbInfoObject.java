package com.homihq.db2rest.rest.meta.db;

public record DbInfoObject(
        String dbId,
        String productName,
        int majorVersion,
        String driverName,
        String driverVersion
) {}
