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
import gg.jte.TemplateEngine;
import gg.jte.TemplateOutput;
import gg.jte.output.StringOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Slf4j
@RequiredArgsConstructor
public class SqlCreatorTemplate {

    private static final String ROOT_TABLE = "rootTable";
    private static final String ROOT_WHERE = "rootWhere";
    private static final String ROOT_TABLE_ALIAS = "rootTableAlias";
    private static final String COLUMNS = "columns";
    private static final String JOINS = "joins";
    private static final String LIMIT = "limit";

    private final TemplateEngine templateEngine;
    private final JdbcManager jdbcManager;

    public String updateQuery(UpdateContext updateContext) {

        Map<String, Object> params = new HashMap<>();
        DbTable table = updateContext.getTable();
        Dialect dialect = jdbcManager.getDialect(updateContext.getDbId());

        params.put(ROOT_TABLE, dialect.supportAlias() ? table.render() : table.name());

        params.put(ROOT_WHERE, updateContext.getWhere());
        params.put("columnSets", updateContext.renderSetColumns());
        params.put(ROOT_TABLE_ALIAS, table.alias());

        return renderSqlTemplate(dialect.getUpdateSqlTemplate(), params);
    }

    public String deleteQuery(DeleteContext deleteContext) {
        Dialect dialect = jdbcManager.getDialect(deleteContext.getDbId());

        String rendererTableName = dialect.renderTableName(
                deleteContext.getTable(),
                StringUtils.isNotBlank(deleteContext.getWhere()),
                true
        );

        log.info("rendererTableName - {}", rendererTableName);

        Map<String, Object> params = new HashMap<>();
        params.put(ROOT_TABLE, rendererTableName);
        params.put(ROOT_WHERE, deleteContext.getWhere());
        params.put(ROOT_TABLE_ALIAS, deleteContext.getTable().alias());

        return this.renderSqlTemplate(dialect.getDeleteSqlTemplate(), params);
    }

    public String create(CreateContext createContext) {

        Map<String, Object> params = new HashMap<>();

        params.put("table", createContext.table().fullName());
        params.put(COLUMNS, createContext.renderColumns());
        params.put("parameters", createContext.renderParams());

        Dialect dialect = jdbcManager.getDialect(createContext.dbId());

        return this.renderSqlTemplate(dialect.getInsertSqlTemplate(), params);
    }

    public String findOne(ReadContext readContext) {
        Map<String, Object> params = new HashMap<>();
        params.put(COLUMNS, projections(readContext.getCols()));
        params.put(ROOT_TABLE, readContext.getRoot().render());
        params.put(ROOT_WHERE, readContext.getRootWhere());

        Dialect dialect = jdbcManager.getDialect(readContext.getDbId());

        return this.renderSqlTemplate(dialect.getFindOneSqlTemplate(), params);
    }

    public String count(ReadContext readContext) {
        Map<String, Object> params = new HashMap<>();

        params.put(ROOT_TABLE, readContext.getRoot().render());
        params.put(ROOT_WHERE, readContext.getRootWhere());

        Dialect dialect = jdbcManager.getDialect(readContext.getDbId());

        return this.renderSqlTemplate(dialect.getCountSqlTemplate(), params);
    }

    public String exists(ReadContext readContext) {
        Map<String, Object> params = new HashMap<>();

        params.put(ROOT_TABLE, readContext.getRoot().render());
        params.put(ROOT_WHERE, readContext.getRootWhere());
        params.put(JOINS, readContext.getDbJoins());

        Dialect dialect = jdbcManager.getDialect(readContext.getDbId());

        return this.renderSqlTemplate(dialect.getExistSqlTemplate(), params);
    }

    public String query(ReadContext readContext) {

        log.debug("**** Preparing to render ****");

        Map<String, Object> params = new HashMap<>();
        params.put(COLUMNS, projections(readContext.getCols()));
        params.put(ROOT_TABLE, readContext.getRoot().render());
        params.put(ROOT_WHERE, readContext.getRootWhere());
        params.put(JOINS, readContext.getDbJoins());

        if (Objects.nonNull(readContext.getDbSortList())
                && !readContext.getDbSortList().isEmpty()) {
            params.put("sorts", orderBy(readContext.getDbSortList()));
        }


        log.debug("limit - {}", readContext.getLimit());
        log.debug("offset - {}", readContext.getOffset());


        if (readContext.getLimit() > -1) {
            params.put(LIMIT, readContext.getLimit());
        }

        if (readContext.getLimit() == -1) {
            params.put(LIMIT, readContext.getDefaultFetchLimit());
        }

        if (readContext.getOffset() > -1) {
            params.put("offset", readContext.getOffset());
        }

        log.debug("data - {}", params);

        Dialect dialect = jdbcManager.getDialect(readContext.getDbId());

        return this.renderSqlTemplate(dialect.getReadSqlTemplate(), params);
    }

    private String renderSqlTemplate(String template, Map<String, Object> params) {
        TemplateOutput output = new StringOutput();

        this.templateEngine.render(template + ".jte", params, output);

        return output.toString();
    }

    private String projections(List<DbColumn> columns) {
        List<String> columList = columns.stream().map(DbColumn::renderWithAlias).toList();

        return StringUtils.join(columList, "\n\t,");
    }

    private String orderBy(List<DbSort> sorts) {
        List<String> sortList =
                sorts.stream().map(DbSort::render).toList();

        return StringUtils.join(sortList, "\n\t,");
    }

}
