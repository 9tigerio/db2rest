package com.homihq.db2rest.rest.update;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.exception.InvalidTableException;
import com.homihq.db2rest.mybatis.MyBatisTable;
import com.homihq.db2rest.rsql.operators.CustomRSQLOperators;
import com.homihq.db2rest.rsql.v2.parser.MyBatisFilterVisitor;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schemacrawler.schema.Table;
import java.util.Map;
import static org.mybatis.dynamic.sql.update.UpdateDSL.update;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateService {

    private final Db2RestConfigProperties db2RestConfigProperties;
    private final SchemaManager schemaManager;
    @Transactional
    public void patch(String schemaName, String tableName, Map<String,Object> data, String filter) {
        db2RestConfigProperties.verifySchema(schemaName);

        Table t = schemaManager.getTable(schemaName, tableName)
                .orElseThrow(() -> new InvalidTableException(tableName));

        MyBatisTable table = new MyBatisTable(schemaName, tableName, t);

        UpdateDSL<UpdateModel> updateDSL = update(table);

        for(String key : data.keySet()) {
            updateDSL.set(table.column(key)).equalToOrNull(data.get(key));
        }
        if(StringUtils.isNotBlank(filter)) {

            Node rootNode = new RSQLParser(CustomRSQLOperators.customOperators()).parse(filter);

            SqlCriterion condition = rootNode
                    .accept(new MyBatisFilterVisitor(table));

            updateDSL.where(condition);
        }

        UpdateStatementProvider updateStatement = updateDSL.build()
                .render(RenderingStrategies.SPRING_NAMED_PARAMETER);

        String sql = updateStatement.getUpdateStatement();
        Map<String,Object> bindValues = updateStatement.getParameters();


        log.info("SQL - {}", sql);
        log.info("Bind variables - {}", bindValues);
    }

}
