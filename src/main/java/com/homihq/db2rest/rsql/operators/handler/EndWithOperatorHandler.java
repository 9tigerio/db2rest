package com.homihq.db2rest.rsql.operators.handler;


import com.homihq.db2rest.rsql.operators.OperatorHandler;
import org.jooq.Condition;

import static org.jooq.impl.DSL.field;

public class EndWithOperatorHandler implements OperatorHandler {

    private static final String OPERATOR = " like ";

    @Override
    public Condition handle(String columnName, String value, Class type) {

        return
                field(columnName)
                        .like("'%" + value + "'");
    }

}
