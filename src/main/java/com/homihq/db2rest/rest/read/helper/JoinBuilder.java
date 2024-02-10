package com.homihq.db2rest.rest.read.helper;

import com.homihq.db2rest.mybatis.MyBatisTable;
import com.homihq.db2rest.schema.SchemaManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.AndOrCriteriaGroup;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.homihq.db2rest.schema.NameUtil.getAlias;
import static org.mybatis.dynamic.sql.SqlBuilder.equalTo;
import static org.mybatis.dynamic.sql.SqlBuilder.or;

@Component
@Slf4j
@RequiredArgsConstructor
public class JoinBuilder  {

    private final SchemaManager schemaManager;

    public void build(ReadContext context) {

        if(context.isUnion()) return;

        List<MyBatisTable> tableList = context.getTables();

        log.info("Table list - {}", tableList);

        int counter = tableList.size() + 1;

        //start path walking for implicit joins

        createJoins(context, tableList, counter);

    }

    private void createJoins(ReadContext context, List<MyBatisTable> tableList, int counter) {
        if(tableList.size() > 1) { //table join required
            for(int i = 0; i < tableList.size() ; i = i + 2) {
                MyBatisTable root = tableList.get(i);
                MyBatisTable child = tableList.get(i + 1);

                List<ForeignKey> foreignKeys = schemaManager.getForeignKeysBetween(root, child);

                log.debug("Foreign keys - {}", foreignKeys);

                int fk = 0;
                for(ForeignKey foreignKey : foreignKeys) {

                    log.debug("ForeignKey reference - {}", foreignKey);

                    for(ColumnReference columnReference : foreignKey.getColumnReferences()) {

                        log.debug("Column reference - {}", columnReference);

                        if(fk == 0) {
                            context.queryExpressionDSL
                                    .join(child)
                                    .on(
                                            child.column(columnReference.getPrimaryKeyColumn().getName())
                                            , equalTo(root.column(columnReference.getForeignKeyColumn().getName()))
                                    )


                            ;

                        }
                        else {

                            if(columnReference.getForeignKeyColumn().isNullable()) {
                                MyBatisTable tChild = child.withAlias(getAlias(counter, ""));
                                context.queryExpressionDSL
                                        .leftJoin(tChild)
                                        .on(
                                                tChild.column(columnReference.getPrimaryKeyColumn().getName())
                                                , equalTo(root.column(columnReference.getForeignKeyColumn().getName()))
                                        );
                            }
                            else {
                                MyBatisTable tChild = child.withAlias(getAlias(counter, ""));
                                context.queryExpressionDSL
                                        .join(tChild)
                                        .on(
                                                tChild.column(columnReference.getPrimaryKeyColumn().getName())
                                                , equalTo(root.column(columnReference.getForeignKeyColumn().getName()))
                                        );
                            }
                            counter++;
                        }
                    }

                    fk++;
                }
            }

        }
    }

}
