package com.homihq.db2rest.rsql.operators.handler;

import com.homihq.db2rest.rsql.operators.OperatorHandler;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import static org.jooq.impl.DSL.*;

@Slf4j
public class EqualToOperatorHandler implements OperatorHandler {

    @Override
    public Condition handle(String columnName, String value, Class type) {
        log.info("Type - {}", type);

        return
        field(columnName).eq(val(parseValue(value,type)));


    }

}
