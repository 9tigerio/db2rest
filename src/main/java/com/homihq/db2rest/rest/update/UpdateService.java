package com.homihq.db2rest.rest.update;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.dbop.JdbcOperationService;
import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.model.DbTable;
import com.homihq.db2rest.model.DbWhere;
import com.homihq.db2rest.rest.update.dto.UpdateContext;

import com.homihq.db2rest.rsql2.parser.RSQLParserBuilder;
import com.homihq.db2rest.rsql2.visitor.BaseRSQLVisitor;
import com.homihq.db2rest.schema.SchemaManager;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateService {

    private final Db2RestConfigProperties db2RestConfigProperties;
    private final SchemaManager schemaManager;
    private final UpdateCreatorTemplate updateCreatorTemplate;
    private final JdbcOperationService jdbcOperationService;

    @Transactional
    public int patch(String schemaName, String tableName, Map<String,Object> data, String filter) {

        DbTable dbTable;
        if(db2RestConfigProperties.getMultiTenancy().isSchemaBased()) {
            //Only relevant for schema per tenant multi tenancy
            //TODO - handle schema retrieval from request
            dbTable = schemaManager.getOneTableV2(schemaName, tableName);
        }
        else{
            //get a unique table
            dbTable = schemaManager.getTable(tableName);
        }

        List<String> updatableColumns =
            data.keySet().stream().toList();

        this.schemaManager.getDialect().processTypes(dbTable, updatableColumns, data);

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

        log.info("{}", sql);
        log.info("{}", context.getParamMap());

        try {
            return jdbcOperationService.update(context.getParamMap(), sql);
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
                            dbWhere, schemaManager.getDialect()));
            context.setWhere(where);

        }
    }

}
