package com.homihq.db2rest.jdbc.sql;

import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.rest.delete.dto.DeleteContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class DeleteCreatorTemplate {

    private final SpringTemplateEngine templateEngine;
    private final Dialect dialect;
    public String deleteQuery(DeleteContext deleteContext) {

        String rendererTableName = dialect.supportAlias() ? deleteContext.getTable().render()
                : deleteContext.getTable().name();

        Map<String,Object> data = new HashMap<>();
        data.put("rootTable", rendererTableName);
        data.put("rootWhere", deleteContext.getWhere());

        Context context = new Context();
        context.setVariables(data);
        return templateEngine.process("delete", context);

    }
}
