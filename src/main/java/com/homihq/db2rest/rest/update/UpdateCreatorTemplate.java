package com.homihq.db2rest.rest.update;

import com.homihq.db2rest.rest.delete.dto.DeleteContext;
import com.homihq.db2rest.rest.update.dto.UpdateContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;


@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateCreatorTemplate {


    private final SpringTemplateEngine templateEngine;
    public String updateQuery(UpdateContext updateContext) {

        Map<String,Object> data = new HashMap<>();

        data.put("rootTable", updateContext.getTable().render());
        data.put("rootWhere", updateContext.getWhere());
        data.put("columnSets", updateContext.renderSetColumns());

        Context context = new Context();
        context.setVariables(data);
        return templateEngine.process("update", context);

    }



}
