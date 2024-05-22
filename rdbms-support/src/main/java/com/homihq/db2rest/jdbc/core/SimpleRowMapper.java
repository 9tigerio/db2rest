package com.homihq.db2rest.jdbc.core;

import com.homihq.db2rest.jdbc.config.dialect.Dialect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.ColumnMapRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class SimpleRowMapper extends ColumnMapRowMapper {

    private final Dialect dialect;

    @Override
    protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
        //HOT FIX - to be moved to dialect
        String columnType = rs.getMetaData().getColumnTypeName(index);

        log.debug("columnType - {}", columnType);

        if(StringUtils.equalsIgnoreCase(columnType, "_varchar")) { //handle pg varchar array
            return dialect.convertToStringArray(rs.getArray(index));
        }
        if(StringUtils.equalsAnyIgnoreCase(columnType, "json","jsonb")) { //handle pg jsonb, json
            return dialect.convertJsonToMap(rs.getObject(index));
        }

        return super.getColumnValue(rs, index);
    }
}
