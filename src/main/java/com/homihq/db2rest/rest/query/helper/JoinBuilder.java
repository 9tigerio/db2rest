package com.homihq.db2rest.rest.query.helper;

import com.homihq.db2rest.mybatis.MyBatisTable;
import com.homihq.db2rest.schema.SchemaManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;

import org.springframework.stereotype.Component;
import java.util.List;
import static org.mybatis.dynamic.sql.SqlBuilder.equalTo;

@Component
@Slf4j
@RequiredArgsConstructor
public class JoinBuilder  {

    private final SchemaManager schemaManager;

    public void build(QueryContext context) {
        List<MyBatisTable> tableList = context.getTables();

        if(tableList.size() > 1) { //table join required
            for(int i = 0 ; i < tableList.size() ; i = i + 2) {
                MyBatisTable root = tableList.get(i);
                MyBatisTable child = tableList.get(i + 1);

                List<ForeignKey> foreignKeys = schemaManager.getForeignKeysBetween(context.getSchemaName(),
                        root.getTableName(),
                        child.getTableName());

                for(ForeignKey foreignKey : foreignKeys) {

                    for(ColumnReference columnReference : foreignKey.getColumnReferences()) {

                        context.queryExpressionDSL
                                .join(child)
                                .on(
                                        child.column(columnReference.getPrimaryKeyColumn().getName())
                                        ,equalTo(root.column(columnReference.getForeignKeyColumn().getName())
                                ));


                    }
                }
            }

        }

    }

}
