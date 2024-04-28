package com.homihq.db2rest.core.config;

import lombok.Data;

@Data
public class DateTimeConfigProperties {
    boolean enableDataTimeFormatting;
    String timeFormat;
    String dateFormat;
    String dateTimeFormat;
}
