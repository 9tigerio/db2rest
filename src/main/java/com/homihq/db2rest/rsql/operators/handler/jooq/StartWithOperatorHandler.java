package com.homihq.db2rest.rsql.operators.handler.jooq;

import org.jooq.Condition;

import static org.jooq.impl.DSL.field;

public class StartWithOperatorHandler implements JooqOperatorHandler {

    private static final String OPERATOR = " like ";

    @Override
    public Condition handle(String columnName, String value, Class type) {


        return
                field(columnName)
                        .like("'" + value + "%'");
    }

}
