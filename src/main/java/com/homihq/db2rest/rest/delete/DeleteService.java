package com.homihq.db2rest.rest.delete;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.exception.DeleteOpNotAllowedException;
import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.exception.InvalidTableException;
import com.homihq.db2rest.mybatis.MyBatisTable;
import com.homihq.db2rest.rsql.operators.SimpleRSQLOperators;
import com.homihq.db2rest.rsql.parser.MyBatisFilterVisitor;
import com.homihq.db2rest.schema.SchemaManager;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.SqlCriterion;
import org.mybatis.dynamic.sql.delete.DeleteDSL;
import org.mybatis.dynamic.sql.delete.DeleteModel;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import schemacrawler.schema.Table;

import static org.mybatis.dynamic.sql.delete.DeleteDSL.deleteFrom;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteService {

    private final Db2RestConfigProperties db2RestConfigProperties;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SchemaManager schemaManager;

    @Transactional
    public int delete(String schemaName, String tableName, String filter) {

        if (StringUtils.isBlank(filter) && db2RestConfigProperties.isAllowSafeDelete()) {
            throw new DeleteOpNotAllowedException(true);
        } else {

            db2RestConfigProperties.verifySchema(schemaName);

            Table t = schemaManager.getTable(schemaName, tableName)
                    .orElseThrow(() -> new InvalidTableException(tableName));

            var table = new MyBatisTable(schemaName, tableName, t);

            DeleteDSL<DeleteModel> deleteDSL = deleteFrom(table);

            if (StringUtils.isNotBlank(filter)) {

                Node rootNode = new RSQLParser(SimpleRSQLOperators.customOperators()).parse(filter);

                SqlCriterion condition = rootNode
                        .accept(new MyBatisFilterVisitor(table));

                deleteDSL.where(condition);
            }

            DeleteStatementProvider deleteStatement = deleteDSL.build()
                    .render(RenderingStrategies.SPRING_NAMED_PARAMETER);

            log.info("SQL - {}", deleteStatement.getDeleteStatement());
            log.info("Bind variables - {}", deleteStatement.getParameters());

            try {
                return namedParameterJdbcTemplate.update(deleteStatement.getDeleteStatement(),
                        deleteStatement.getParameters());
            } catch (DataAccessException e) {
                throw new GenericDataAccessException(e.getMostSpecificCause().getMessage());
            }

        }

    }
}
