package com.homihq.db2rest.rsql.operators.handler;

import com.homihq.db2rest.rsql.operators.OperatorHandler;
import org.jooq.Condition;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.val;

public class GreaterThanOperatorHandler implements OperatorHandler {

    @Override
    public Condition handle(String columnName, String value, Class type) {

        return
                field(columnName).gt(val(parseValue(value,type)));
    }

}
