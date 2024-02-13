package com.homihq.db2rest.rest.delete;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.model.DbWhere;
import com.homihq.db2rest.model.DbTable;
import com.homihq.db2rest.rest.delete.dto.DeleteContext;
import com.homihq.db2rest.rsql2.parser.RSQLParserBuilder;
import com.homihq.db2rest.rsql2.visitor.BaseRSQLVisitor;
import com.homihq.db2rest.schema.SchemaManager;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteService {

    private final Db2RestConfigProperties db2RestConfigProperties;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SchemaManager schemaManager;
    private final DeleteCreatorTemplate deleteCreatorTemplate;

    @Transactional
    public int delete(String schemaName, String tableName, String filter) {
        db2RestConfigProperties.checkDeleteAllowed(filter);

        DbTable dbTable;
        if(db2RestConfigProperties.getMultiTenancy().isSchemaBased()) {
            //Only relevant for schema per tenant multi tenancy
            //TODO - handle schema retrieval from request
            dbTable = schemaManager.getOneTableV2(schemaName, tableName);
        }
        else{
            //get a unique table
            dbTable = schemaManager.getTableV2(tableName);
        }
        DeleteContext context = DeleteContext.builder()
                .tableName(tableName)
                .table(dbTable).build();



        return executeDelete(filter, dbTable, context);

    }

    private int executeDelete(String filter, DbTable table, DeleteContext context) {

        addWhere(filter, table, context);
        String sql =
        deleteCreatorTemplate.deleteQuery(context);

        log.info("{}", sql);
        log.info("{}", context.getParamMap());

        try {
            return namedParameterJdbcTemplate.update(sql,
                    context.getParamMap());
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
                            dbWhere));
            context.setWhere(where);

        }
    }
}
