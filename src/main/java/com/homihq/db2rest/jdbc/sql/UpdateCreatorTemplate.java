package com.homihq.db2rest.jdbc.sql;

import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.rest.update.dto.UpdateContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
public class UpdateCreatorTemplate {

    private final SpringTemplateEngine templateEngine;
    private final Dialect dialect;
    public String updateQuery(UpdateContext updateContext) {

        Map<String,Object> data = new HashMap<>();

        if(dialect.supportAlias()) {
            data.put("rootTable", updateContext.getTable().render());
        }
        else{
            data.put("rootTable", updateContext.getTable().name());
        }

        data.put("rootWhere", updateContext.getWhere());
        data.put("columnSets", updateContext.renderSetColumns());

        Context context = new Context();
        context.setVariables(data);
        return templateEngine.process("update", context);

    }



}
