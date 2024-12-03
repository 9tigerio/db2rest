package com.homihq.db2rest.jdbc.rsql.operator.handler;

import com.homihq.db2rest.jdbc.config.dialect.Dialect;
import com.homihq.db2rest.jdbc.config.model.DbColumn;
import com.homihq.db2rest.jdbc.config.model.DbWhere;
import com.homihq.db2rest.jdbc.rsql.operator.OperatorHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class GreaterThanOperatorHandler implements OperatorHandler {

    private static final String OPERATOR = " > ";

    @Override
    public String handle(Dialect dialect, DbColumn column, DbWhere dbWhere, String value, Class<?> type, Map<String, Object> paramMap) {

        log.debug("value - {}", value);
        log.debug("type - {}", type);

        Object vo = dialect.processValue(value, type, null);

        if (dialect.supportAlias()) {
            String key =
                    reviewAndSetParam(dialect.getAliasedNameParam(column, dbWhere.isDelete()), vo, paramMap);
            return dialect.getAliasedName(column, dbWhere.isDelete()) + OPERATOR + PREFIX + key;
        } else {

            String key = reviewAndSetParam(column.name(), vo, paramMap);
            return column.name() + OPERATOR + PREFIX + key;
        }
    }



}
