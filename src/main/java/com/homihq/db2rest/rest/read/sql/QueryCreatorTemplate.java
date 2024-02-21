package com.homihq.db2rest.rest.read.sql;

import com.homihq.db2rest.model.DbSort;
import com.homihq.db2rest.rest.read.dto.ReadContext;
import com.homihq.db2rest.model.DbColumn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Component
@Slf4j
@RequiredArgsConstructor
public class QueryCreatorTemplate {


    private final SpringTemplateEngine templateEngine;

    public String createFindOneQuery(ReadContext readContext) {
        Map<String,Object> data = new HashMap<>();
        data.put("columns", createProjections(readContext.getCols()));
        data.put("rootTable", readContext.getRoot().render());
        data.put("rootWhere", readContext.getRootWhere());

        Context context = new Context();
        context.setVariables(data);
        return templateEngine.process("find-one", context);
    }

    public String createCountQuery(ReadContext readContext) {
        Map<String,Object> data = new HashMap<>();

        data.put("rootTable", readContext.getRoot().render());
        data.put("rootWhere", readContext.getRootWhere());

        Context context = new Context();
        context.setVariables(data);
        return templateEngine.process("count", context);
    }

    public String createQuery(ReadContext readContext) {

        log.info("**** Preparing to render ****");

        Map<String,Object> data = new HashMap<>();
        data.put("columns", createProjections(readContext.getCols()));
        data.put("rootTable", readContext.getRoot().render());
        data.put("rootWhere", readContext.getRootWhere());
        data.put("joins", readContext.getDbJoins());

        if(Objects.nonNull(readContext.getDbSortList()) && !readContext.getDbSortList().isEmpty()) {
            data.put("sorts", createOrderBy(readContext.getDbSortList()));
        }


        if(readContext.getLimit() > -1) data.put("limit", readContext.getLimit());
        if(readContext.getOffset() > -1) data.put("offset", readContext.getOffset());


        Context context = new Context();
        context.setVariables(data);
        return templateEngine.process("read", context);

    }

    public String createProjections(List<DbColumn> columns) {
        List<String> columList =
        columns.stream().map(DbColumn::renderWithAlias).toList();

        return StringUtils.join(columList, "\n\t,");
    }

    public String createOrderBy(List<DbSort> sorts) {
        List<String> sortList =
                sorts.stream().map(DbSort::render).toList();

        return StringUtils.join(sortList, "\n\t,");
    }

}
