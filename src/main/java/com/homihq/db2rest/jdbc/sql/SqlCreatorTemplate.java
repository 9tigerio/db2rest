package com.homihq.db2rest.jdbc.sql;

import com.homihq.db2rest.core.model.DbColumn;
import com.homihq.db2rest.core.model.DbSort;
import com.homihq.db2rest.rest.read.dto.ReadContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Slf4j
@RequiredArgsConstructor
public class SqlCreatorTemplate {


    private final SpringTemplateEngine templateEngine;

    public String findOne(ReadContext readContext) {
        Map<String,Object> data = new HashMap<>();
        data.put("columns", projections(readContext.getCols()));
        data.put("rootTable", readContext.getRoot().render());
        data.put("rootWhere", readContext.getRootWhere());

        Context context = new Context();
        context.setVariables(data);
        return templateEngine.process("find-one", context);
    }

    public String count(ReadContext readContext) {
        Map<String,Object> data = new HashMap<>();

        data.put("rootTable", readContext.getRoot().render());
        data.put("rootWhere", readContext.getRootWhere());

        Context context = new Context();
        context.setVariables(data);
        return templateEngine.process("count", context);
    }

    public String exists(ReadContext readContext) {
        Map<String, Object> data = new HashMap<>();

        data.put("rootTable", readContext.getRoot().render());
        data.put("rootWhere", readContext.getRootWhere());
        data.put("joins", readContext.getDbJoins());

        Context context = new Context();
        context.setVariables(data);

        return templateEngine.process("exists", context);
    }


    public String query(ReadContext readContext) {

        log.debug("**** Preparing to render ****");

        Map<String,Object> data = new HashMap<>();
        data.put("columns", projections(readContext.getCols()));
        data.put("rootTable", readContext.getRoot().render());
        data.put("rootWhere", readContext.getRootWhere());
        data.put("joins", readContext.getDbJoins());

        if(Objects.nonNull(readContext.getDbSortList()) && !readContext.getDbSortList().isEmpty()) {
            data.put("sorts", orderBy(readContext.getDbSortList()));
        }


        if(readContext.getLimit() > -1) data.put("limit", readContext.getLimit());
        if(readContext.getOffset() > -1) data.put("offset", readContext.getOffset());

        //log.info("data - {}", data);


        Context context = new Context();
        context.setVariables(data);
        return templateEngine.process("read", context);

    }

    public String projections(List<DbColumn> columns) {
        List<String> columList =
        columns.stream().map(DbColumn::renderWithAlias).toList();

        return StringUtils.join(columList, "\n\t,");
    }

    public String orderBy(List<DbSort> sorts) {
        List<String> sortList =
                sorts.stream().map(DbSort::render).toList();

        return StringUtils.join(sortList, "\n\t,");
    }

}
