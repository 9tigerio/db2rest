package com.homihq.db2rest.rest.update;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.mybatis.MyBatisTable;
import com.homihq.db2rest.rsql.operators.SimpleRSQLOperators;
import com.homihq.db2rest.rsql.parser.MyBatisFilterVisitorParser;
import com.homihq.db2rest.schema.SchemaManager;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.SqlCriterion;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.UpdateModel;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import static org.mybatis.dynamic.sql.update.UpdateDSL.update;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateService {

    private final Db2RestConfigProperties db2RestConfigProperties;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SchemaManager schemaManager;
    @Transactional
    public int patch(String schemaName, String tableName, Map<String,Object> data, String filter) {

        MyBatisTable table;
        if(db2RestConfigProperties.getMultiTenancy().isSchemaBased()) {
            table = schemaManager.getOneTable(schemaName, tableName);
        } else {
            table = schemaManager.getTable(tableName);
        }

        return executeUpdate(filter, data, table);

    }

    private int executeUpdate(String filter, Map<String, Object> data, MyBatisTable table) {
        UpdateDSL<UpdateModel> updateDSL = update(table);

        for(String key : data.keySet()) {
            updateDSL.set(table.column(key)).equalToOrNull(data.get(key));
        }

        addWhere(filter, table, updateDSL);

        UpdateStatementProvider updateStatement = updateDSL.build()
                .render(RenderingStrategies.SPRING_NAMED_PARAMETER);

        log.info("SQL - {}", updateStatement.getUpdateStatement());
        log.info("Bind variables - {}", updateStatement.getParameters());

        try {
            return namedParameterJdbcTemplate.update(updateStatement.getUpdateStatement(),
                    updateStatement.getParameters());
        } catch (DataAccessException e) {
            throw new GenericDataAccessException(e.getMostSpecificCause().getMessage());
        }
    }

    private void addWhere(String filter, MyBatisTable table, UpdateDSL<UpdateModel> updateDSL) {
        if(StringUtils.isNotBlank(filter)) {

            Node rootNode = new RSQLParser(SimpleRSQLOperators.customOperators()).parse(filter);

            SqlCriterion condition = rootNode
                    .accept(new MyBatisFilterVisitorParser(table));

            updateDSL.where(condition);
        }
    }

}
