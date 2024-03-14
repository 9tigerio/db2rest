package com.homihq.db2rest.jdbc.service;

import com.homihq.db2rest.core.config.Db2RestConfigProperties;
import com.homihq.db2rest.core.DbOperationService;
import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.core.service.UpdateService;
import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.jdbc.sql.UpdateCreatorTemplate;
import com.homihq.db2rest.core.model.DbTable;
import com.homihq.db2rest.core.model.DbWhere;
import com.homihq.db2rest.rest.update.dto.UpdateContext;

import com.homihq.db2rest.jdbc.rsql.parser.RSQLParserBuilder;
import com.homihq.db2rest.jdbc.rsql.visitor.BaseRSQLVisitor;
import com.homihq.db2rest.schema.SchemaCache;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
public class JdbcUpdateService implements UpdateService {

    private final Db2RestConfigProperties db2RestConfigProperties;
    private final SchemaCache schemaCache;
    private final UpdateCreatorTemplate updateCreatorTemplate;
    private final DbOperationService dbOperationService;
    private final Dialect dialect;

    @Override
    @Transactional
    public int patch(String schemaName, String tableName, Map<String, Object> data, String filter) {

        DbTable dbTable = schemaCache.getTable(tableName);


        List<String> updatableColumns =
            data.keySet().stream().toList();

        this.dialect.processTypes(dbTable, updatableColumns, data);

        UpdateContext context = UpdateContext.builder()
                .tableName(tableName)
                .table(dbTable)
                .updatableColumns(updatableColumns)
                .build();

        context.createParamMap(data);

        return executeUpdate(filter, dbTable, context);


    }

    private int executeUpdate(String filter, DbTable table, UpdateContext context) {

        addWhere(filter, table, context);
        String sql =
                updateCreatorTemplate.updateQuery(context);

        log.debug("{}", sql);
        log.debug("{}", context.getParamMap());

        try {
            return dbOperationService.update(context.getParamMap(), sql);
        } catch (DataAccessException e) {
            log.error("Error in delete op : " , e);
            throw new GenericDataAccessException(e.getMostSpecificCause().getMessage());
        }
    }



    private void addWhere(String filter, DbTable table, UpdateContext context) {

        if(StringUtils.isNotBlank(filter)) {

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
