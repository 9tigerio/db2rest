package com.homihq.db2rest.jdbc.service;

import com.homihq.db2rest.core.config.Db2RestConfigProperties;
import com.homihq.db2rest.core.DbOperationService;
import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.core.service.DeleteService;
import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.core.model.DbWhere;
import com.homihq.db2rest.core.model.DbTable;
import com.homihq.db2rest.jdbc.sql.SqlCreatorTemplate;
import com.homihq.db2rest.rest.delete.dto.DeleteContext;
import com.homihq.db2rest.jdbc.rsql.parser.RSQLParserBuilder;
import com.homihq.db2rest.jdbc.rsql.visitor.BaseRSQLVisitor;
import com.homihq.db2rest.schema.SchemaCache;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public class JdbcDeleteService implements DeleteService {

    private final Db2RestConfigProperties db2RestConfigProperties;
    private final SchemaCache schemaCache;
    private final SqlCreatorTemplate sqlCreatorTemplate;
    private final DbOperationService dbOperationService;
    private final Dialect dialect;

    @Override
    @Transactional
    public int delete(String schemaName, String tableName, String filter) {
        db2RestConfigProperties.checkDeleteAllowed(filter);

        DbTable dbTable = schemaCache.getTable(tableName);

        DeleteContext context = DeleteContext.builder()
                .tableName(tableName)
                .table(dbTable).build();



        return executeDelete(filter, dbTable, context);

    }

    private int executeDelete(String filter, DbTable table, DeleteContext context) {

        addWhere(filter, table, context);
        String sql =
                sqlCreatorTemplate.deleteQuery(context);

        log.debug("{}", sql);
        log.debug("{}", context.getParamMap());

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
                    table, null ,context.getParamMap());

            Node rootNode = RSQLParserBuilder.newRSQLParser().parse(filter);

            String where = rootNode
                    .accept(new BaseRSQLVisitor(
                            dbWhere, dialect));
            context.setWhere(where);

        }
    }
}
