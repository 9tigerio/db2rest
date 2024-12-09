package com.homihq.db2rest.rest;

import com.jayway.jsonpath.JsonPath;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    // Convert UTC time to LocalDateTime string
    public static String utcToLocalTimestampString(MvcResult result) throws UnsupportedEncodingException {
        String lastUpdateStr = result.getResponse().getContentAsString();

        String lastUpdateValue = JsonPath.read(lastUpdateStr, "$[0].last_update");
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(lastUpdateValue);

        // Convert OffsetDateTime to LocalDateTime in the system's default time zone
        LocalDateTime localDateTime = offsetDateTime.toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();

        // Format the LocalDateTime into the expected format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return formatter.format(localDateTime);
    }

    // Convert UTC To LocalDateTime Format, Oracle jsonPath format
    public static String utcToLocalTimestampStringOracle(MvcResult result) throws UnsupportedEncodingException {
        String lastUpdateStr = result.getResponse().getContentAsString();

        String lastUpdateValue = JsonPath.read(lastUpdateStr, "$[0].LAST_UPDATE");
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(lastUpdateValue);

        LocalDateTime localDateTime = offsetDateTime.toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return formatter.format(localDateTime);
    }


}
