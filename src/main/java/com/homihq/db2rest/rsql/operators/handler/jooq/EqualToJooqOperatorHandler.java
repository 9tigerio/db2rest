package com.homihq.db2rest.rsql.operators.handler.jooq;

import org.jooq.Condition;
import static org.jooq.impl.DSL.*;


public class EqualToJooqOperatorHandler implements JooqOperatorHandler {


    @Override
    public Condition handle(String columnName, String value, Class type) {
        return
        field(columnName).eq(value);


    }

}
