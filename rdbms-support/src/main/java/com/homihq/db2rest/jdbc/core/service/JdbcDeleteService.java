package com.homihq.db2rest.jdbc.core.service;

import com.homihq.db2rest.jdbc.JdbcSchemaCache;
import com.homihq.db2rest.jdbc.core.DbOperationService;
import com.homihq.db2rest.jdbc.dto.DeleteContext;
import com.homihq.db2rest.jdbc.sql.SqlCreatorTemplate;
import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.jdbc.config.model.DbWhere;
import com.homihq.db2rest.jdbc.config.model.DbTable;
import com.homihq.db2rest.jdbc.rsql.parser.RSQLParserBuilder;
import com.homihq.db2rest.jdbc.rsql.visitor.BaseRSQLVisitor;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public class JdbcDeleteService implements DeleteService {

    private final JdbcSchemaCache jdbcSchemaCache;
    private final SqlCreatorTemplate sqlCreatorTemplate;
    private final DbOperationService dbOperationService;


    @Override
    @Transactional
    public int delete(String schemaName, String tableName, String filter) {


        DbTable dbTable = jdbcSchemaCache.getTable(schemaName,tableName);

        DeleteContext context = DeleteContext.builder()
                .tableName(tableName)
                .table(dbTable).build();



        return executeDelete(filter, dbTable, context);

    }

    private int executeDelete(String filter, DbTable table, DeleteContext context) {

        addWhere(filter, table, context);
        String sql =
                sqlCreatorTemplate.deleteQuery(context);

        log.info("{}", sql);
        log.info("{}", context.getParamMap());

        try {
            return dbOperationService.delete(context.getParamMap(), sql);
        } catch (DataAccessException e) {
            log.error("Error in delete op : " , e);
            throw new GenericDataAccessException(e.getMostSpecificCause().getMessage());
        }
    }



    private void addWhere(String filter, DbTable table, DeleteContext context) {

        if(StringUtils.isNotBlank(filter)) {
            context.createParamMap();

            DbWhere dbWhere = new DbWhere(
                    context.getTableName(),
                    table, null ,context.getParamMap(), "delete");

            Node rootNode = RSQLParserBuilder.newRSQLParser().parse(filter);

            String where = rootNode
                    .accept(new BaseRSQLVisitor(
                            dbWhere, jdbcSchemaCache.getDialect()));
            context.setWhere(where);

        }
    }
}
