package com.homihq.db2rest.multidb;

public record EnvironmentProperties(
        boolean enableDatetimeFormatting,
        String timeFormat,
        String dateFormat,
        String dateTimeFormat,
        int defaultFetchLimit
) {
}
