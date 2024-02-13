package com.homihq.db2rest.rest.delete;

import com.homihq.db2rest.rest.delete.dto.DeleteContext;
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
public class DeleteCreatorTemplate {


    private final SpringTemplateEngine templateEngine;
    public String deleteQuery(DeleteContext deleteContext) {

        Map<String,Object> data = new HashMap<>();

        data.put("rootTable", deleteContext.getTable().render());
        data.put("rootWhere", deleteContext.getWhere());


        Context context = new Context();
        context.setVariables(data);
        return templateEngine.process("delete", context);

    }



}
