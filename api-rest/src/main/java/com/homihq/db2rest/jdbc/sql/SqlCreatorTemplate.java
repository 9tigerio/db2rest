package com.homihq.db2rest.jdbc.sql;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.jdbc.core.model.DbColumn;
import com.homihq.db2rest.jdbc.core.model.DbSort;
import com.homihq.db2rest.jdbc.JdbcSchemaCache;
import com.homihq.db2rest.jdbc.dto.CreateContext;
import com.homihq.db2rest.jdbc.dto.DeleteContext;
import com.homihq.db2rest.jdbc.rest.read.dto.ReadContext;
import com.homihq.db2rest.jdbc.rest.update.dto.UpdateContext;

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
    private final JdbcSchemaCache jdbcSchemaCache;
    private final Db2RestConfigProperties db2RestConfigProperties;

    public String updateQuery(UpdateContext updateContext) {

        Map<String,Object> data = new HashMap<>();

        if(jdbcSchemaCache.getDialect().supportAlias()) {
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

    public String deleteQuery(DeleteContext deleteContext) {

        String rendererTableName =
                jdbcSchemaCache.getDialect()
                        .renderTableName(deleteContext.getTable(),
                                StringUtils.isNotBlank(deleteContext.getWhere()),
                                true

                        );

        log.info("rendererTableName - {}", rendererTableName);

        Map<String,Object> data = new HashMap<>();
        data.put("rootTable", rendererTableName);
        data.put("rootWhere", deleteContext.getWhere());

        Context context = new Context();
        context.setVariables(data);
        return templateEngine.process("delete", context);

    }

    public String create(CreateContext createContext) {

        Map<String,Object> data = new HashMap<>();

        data.put("table", createContext.table().fullName());
        data.put("columns", createContext.renderColumns());
        data.put("parameters", createContext.renderParams());


        Context context = new Context();
        context.setVariables(data);
        return templateEngine.process("insert", context);

    }

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


        log.debug("limit - {}", readContext.getLimit());
        log.debug("offset - {}", readContext.getOffset());


        if(readContext.getLimit() > -1) data.put("limit", readContext.getLimit());
        if(readContext.getLimit() == -1) data.put("limit", db2RestConfigProperties.getDefaultFetchLimit());

        if(readContext.getOffset() > -1) data.put("offset", readContext.getOffset());

        String template = "read";

        log.debug("Product family - {}", this.jdbcSchemaCache.getDialect().getProductFamily());
        log.debug("Product family - {}", this.jdbcSchemaCache.getDialect().getMajorVersion());

        //TODO DB specific processing must move away
        if(StringUtils.equalsIgnoreCase(this.jdbcSchemaCache.getDialect().getProductFamily(), "Oracle")) {

            if(this.jdbcSchemaCache.getDialect().getMajorVersion() >= 12) {
                template = "read-ora-12";
            }
            else {
                template = "read-ora-9";
            }
        }
        log.debug("template - {}", template);
        log.debug("data - {}", data);
        Context context = new Context();
        context.setVariables(data);
        return templateEngine.process(template, context);

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
