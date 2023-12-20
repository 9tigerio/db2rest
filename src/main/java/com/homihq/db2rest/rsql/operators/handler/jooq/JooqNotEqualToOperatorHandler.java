package com.homihq.db2rest.rsql.operators.handler.jooq;

import com.homihq.db2rest.rsql.operators.handler.OperatorHandler;
import org.jooq.Condition;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.val;


public class JooqNotEqualToOperatorHandler implements JooqOperatorHandler {

   private static final String OPERATOR = " != ";

    @Override
    public Condition handle(String columnName, String value, Class type) {
       // return columnName + OPERATOR + parseValue(value, type);

        return
                field(columnName).ne(val(value));
    }

}
