package com.homihq.db2rest.jdbc.core.service;

import com.homihq.db2rest.jdbc.JdbcManager;
import com.homihq.db2rest.jdbc.core.DbOperationService;
import com.homihq.db2rest.jdbc.dto.UpdateContext;
import com.homihq.db2rest.jdbc.sql.SqlCreatorTemplate;
import com.homihq.db2rest.core.exception.GenericDataAccessException;

import com.homihq.db2rest.jdbc.config.model.DbTable;
import com.homihq.db2rest.jdbc.config.model.DbWhere;

import com.homihq.db2rest.jdbc.rsql.parser.RSQLParserBuilder;
import com.homihq.db2rest.jdbc.rsql.visitor.BaseRSQLVisitor;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Slf4j
@RequiredArgsConstructor
public class JdbcUpdateService implements UpdateService {

    private final JdbcManager jdbcManager;
    private final SqlCreatorTemplate sqlCreatorTemplate;
    private final DbOperationService dbOperationService;

    @Override
    public int patch(String dbId, String schemaName, String tableName, Map<String, Object> data, String filter) {

        DbTable dbTable = jdbcManager.getTable(dbId, schemaName, tableName);

        List<String> updatableColumns = data.keySet().stream().toList();

        jdbcManager.getDialect(dbId).processTypes(dbTable, updatableColumns, data);

        UpdateContext context = UpdateContext.builder()
                .dbId(dbId)
                .tableName(tableName)
                .table(dbTable)
                .updatableColumns(updatableColumns)
                .build();

        context.createParamMap(data);

        return executeUpdate(dbId, filter, dbTable, context);


    }

    private int executeUpdate(String dbId,String filter, DbTable table, UpdateContext context) {

        addWhere(filter, table, context);
        String sql =
                sqlCreatorTemplate.updateQuery(context);

        log.debug("{}", sql);
        log.debug("{}", context.getParamMap());


        Integer i = this.jdbcManager.getTxnTemplate(dbId).execute(status -> {
            try {
                return dbOperationService.update(
                        jdbcManager.getNamedParameterJdbcTemplate(dbId),
                        context.getParamMap(), sql);
            } catch (DataAccessException e) {
                log.error("Error in delete op : " , e);
                status.setRollbackOnly();
                throw new GenericDataAccessException(e.getMostSpecificCause().getMessage());
            }

        });

        return Objects.isNull(i) ? 0 : i;
    }



    private void addWhere(String filter, DbTable table, UpdateContext context) {

        if(StringUtils.isNotBlank(filter)) {

            DbWhere dbWhere = new DbWhere(
                    context.getTableName(),
                    table, null ,context.getParamMap(), "update");

            Node rootNode = RSQLParserBuilder.newRSQLParser().parse(filter);

            String where = rootNode
                    .accept(new BaseRSQLVisitor(
                            dbWhere, jdbcManager.getDialect(context.getDbId())));
            context.setWhere(where);

        }
    }

}
