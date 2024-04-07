package com.homihq.db2rest.jdbc.rsql.operator.handler;

import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.core.model.DbColumn;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class NotInOperatorHandler implements OperatorHandler {

   private static final String OPERATOR = " not in ";

    @Override
    public String handle(Dialect dialect, DbColumn columnName, String value, Class type, Map<String, Object> paramMap) {
        return handle(dialect, columnName, Arrays.asList(value), type, paramMap);
    }

    @Override
    public String handle(Dialect dialect, DbColumn column, List<String> values, Class type, Map<String, Object> paramMap) {
        log.info("column - {}", column.name());
        log.info("values - {}", values);
        log.info("type - {}", type);

        List<Object> dataItems = parseListValues(values, type);

        if(dialect.supportAlias()) {

            paramMap.put(column.getAliasedNameParam(), dataItems);
            return column.getAliasedName() + OPERATOR +" ( "+ PREFIX + column.getAliasedNameParam() + " ) ";
        }
        else{
            paramMap.put(column.name(), dataItems);
            return column.name() + OPERATOR + " ( " + PREFIX + column.name() + " ) ";
        }
    }

}
