package com.homihq.db2rest.template;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class SqlRenderer {

    private final SpringTemplateEngine templateEngine;


    public void render(String template, Map<String,Object> data) {
        Context context = new Context();
        context.setVariables(data);
        String sql = templateEngine.process(template, context);
        log.info("sql - {}", sql);
    }

}
