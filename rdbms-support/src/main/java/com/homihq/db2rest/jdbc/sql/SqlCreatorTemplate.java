package com.homihq.db2rest.jdbc.sql;

import com.homihq.db2rest.jdbc.JdbcManager;
import com.homihq.db2rest.jdbc.config.dialect.Dialect;
import com.homihq.db2rest.jdbc.config.model.DbColumn;
import com.homihq.db2rest.jdbc.config.model.DbSort;
import com.homihq.db2rest.jdbc.config.model.DbTable;
import com.homihq.db2rest.jdbc.dto.CreateContext;
import com.homihq.db2rest.jdbc.dto.DeleteContext;
import com.homihq.db2rest.jdbc.dto.ReadContext;
import com.homihq.db2rest.jdbc.dto.UpdateContext;
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
    private final JdbcManager jdbcManager;


    public String updateQuery(UpdateContext updateContext) {

        Map<String,Object> data = new HashMap<>();
        DbTable table = updateContext.getTable();
        Dialect dialect = jdbcManager.getDialect(updateContext.getDbId());

        if(dialect.supportAlias()) {
            data.put("rootTable", table.render());
        }
        else{
            data.put("rootTable", table.name());
        }

        data.put("rootWhere", updateContext.getWhere());
        data.put("columnSets", updateContext.renderSetColumns());
        data.put("rootTableAlias", table.alias());

        Context context = new Context();
        context.setVariables(data);

        return templateEngine.process(dialect.getUpdateSqlTemplate(), context);
    }

    public String deleteQuery(DeleteContext deleteContext) {
        Dialect dialect = jdbcManager.getDialect(deleteContext.getDbId());

        String rendererTableName = dialect.renderTableName(
            deleteContext.getTable(),
            StringUtils.isNotBlank(deleteContext.getWhere()),
            true
        );

        log.info("rendererTableName - {}", rendererTableName);

        Map<String,Object> data = new HashMap<>();
        data.put("rootTable", rendererTableName);
        data.put("rootWhere", deleteContext.getWhere());
        data.put("rootTableAlias", deleteContext.getTable().alias());

        Context context = new Context();
        context.setVariables(data);

        return templateEngine.process(dialect.getDeleteSqlTemplate(), context);
    }

    public String create(CreateContext createContext) {

        Map<String,Object> data = new HashMap<>();

        data.put("table", createContext.table().fullName());
        data.put("columns", createContext.renderColumns());
        data.put("parameters", createContext.renderParams());

        Context context = new Context();
        context.setVariables(data);
        Dialect dialect = jdbcManager.getDialect(createContext.dbId());

        return templateEngine.process(dialect.getInsertSqlTemplate(), context);
    }

    public String findOne(ReadContext readContext) {
        Map<String,Object> data = new HashMap<>();
        data.put("columns", projections(readContext.getCols()));
        data.put("rootTable", readContext.getRoot().render());
        data.put("rootWhere", readContext.getRootWhere());

        Context context = new Context();
        context.setVariables(data);
        Dialect dialect = jdbcManager.getDialect(readContext.getDbId());

        return templateEngine.process(dialect.getFindOneSqlTemplate(), context);
    }

    public String count(ReadContext readContext) {
        Map<String,Object> data = new HashMap<>();

        data.put("rootTable", readContext.getRoot().render());
        data.put("rootWhere", readContext.getRootWhere());

        Context context = new Context();
        context.setVariables(data);
        Dialect dialect = jdbcManager.getDialect(readContext.getDbId());

        return templateEngine.process(dialect.getCountSqlTemplate(), context);
    }

    public String exists(ReadContext readContext) {
        Map<String, Object> data = new HashMap<>();

        data.put("rootTable", readContext.getRoot().render());
        data.put("rootWhere", readContext.getRootWhere());
        data.put("joins", readContext.getDbJoins());

        Context context = new Context();
        context.setVariables(data);
        Dialect dialect = jdbcManager.getDialect(readContext.getDbId());

        return templateEngine.process(dialect.getExistSqlTemplate(), context);
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
        if(readContext.getLimit() == -1) data.put("limit", readContext.getDefaultFetchLimit());

        if(readContext.getOffset() > -1) data.put("offset", readContext.getOffset());

        log.debug("data - {}", data);
        Context context = new Context();
        context.setVariables(data);
        Dialect dialect = jdbcManager.getDialect(readContext.getDbId());

        return templateEngine.process(dialect.getReadSqlTemplate(), context);
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
