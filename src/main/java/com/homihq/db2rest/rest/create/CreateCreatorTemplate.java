package com.homihq.db2rest.rest.create;

import com.homihq.db2rest.rest.create.dto.CreateContext;
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
public class CreateCreatorTemplate {


    private final SpringTemplateEngine templateEngine;
    public String createQuery(CreateContext createContext) {

        Map<String,Object> data = new HashMap<>();

        data.put("table", createContext.table().fullName());
        data.put("columns", createContext.renderColumns());
        data.put("parameters", createContext.renderParams());


        Context context = new Context();
        context.setVariables(data);
        return templateEngine.process("insert", context);

    }



}
