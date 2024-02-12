package com.homihq.db2rest.rest.read.processor;

import com.homihq.db2rest.rest.read.dto.ReadContextV2;
import com.homihq.db2rest.rest.read.model.DbColumn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
@RequiredArgsConstructor
public class QueryCreatorTemplate {


    private final SpringTemplateEngine templateEngine;
    public String createQuery(ReadContextV2 readContextV2) {

        log.info("**** Preparing to render ****");

        Map<String,Object> data = new HashMap<>();
        data.put("columns", createProjections(readContextV2.getCols()));
        data.put("rootTable", readContextV2.getRoot());
        data.put("rootWhere", readContextV2.getRootWhere());

        Context context = new Context();
        context.setVariables(data);
        return templateEngine.process("read", context);

    }

    public String createProjections(List<DbColumn> columns) {
        List<String> columList =
        columns.stream().map(DbColumn::render).toList();

        return StringUtils.join(columList, "\n\t,");
    }



}
