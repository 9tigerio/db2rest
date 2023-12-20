package com.homihq.db2rest.rsql.operators.handler.jooq;

import com.homihq.db2rest.rsql.operators.handler.OperatorHandler;
import org.jooq.Condition;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.val;

public class JooqNotInOperatorHandler implements JooqOperatorHandler {

   private static final String OPERATOR = " not in ";

    @Override
    public Condition handle(String columnName, String value, Class type) {
       // return handle(columnName, Arrays.asList(value), type);

        return
                field(columnName).notIn(val(Arrays.asList(value)));
    }

    @Override
    public Condition handle(String columnName, List<String> values, Class type) {
        //return columnName + " not in (" +
        //    values.stream().map(value -> parseValue(value, type)).collect(Collectors.joining(",")) + ")";

        return
                field(columnName).notIn(val(values)); //
    }

}
