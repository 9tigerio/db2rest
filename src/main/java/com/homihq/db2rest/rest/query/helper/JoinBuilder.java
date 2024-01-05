package com.homihq.db2rest.rest.query.helper;

import com.homihq.db2rest.rest.query.model.RJoin;
import com.homihq.db2rest.rest.query.model.RTable;
import com.homihq.db2rest.schema.SchemaManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.homihq.db2rest.schema.NameUtil.getAlias;

@Component
@Slf4j
@RequiredArgsConstructor
public class JoinBuilder implements SqlQueryPartBuilder {

    private final SchemaManager schemaManager;

    public void build(QueryContext context) {
        List<RTable> rTableList = context.getRTables();
        List<RJoin> joins = new ArrayList<>();

        if(rTableList.size() > 1) { //table join required
            for(int i = 0 ; i < rTableList.size() ; i = i + 2) {
                RTable root = rTableList.get(i);
                RTable child = rTableList.get(i + 1);

                List<ForeignKey> foreignKeys =
                schemaManager.getForeignKeysBetween(context.getSchemaName(), root.getName(), child.getName());

                int counter = 0;

                for(ForeignKey foreignKey : foreignKeys) {

                    for(ColumnReference columnReference : foreignKey.getColumnReferences()) {
                        RJoin join = createJoin(context, columnReference, counter, root);
                        joins.add(join);
                        counter++;
                    }
                }
            }

            context.setRJoins(joins);
        }

    }



    private RJoin createJoin(QueryContext context, ColumnReference columnReference, int counter, RTable root) {
        RJoin join = new RJoin();
        join.setSchemaName(context.getSchemaName());
        join.setTableName(columnReference.getPrimaryKeyColumn().getParent().getName());
        join.setAlias(getAlias(counter, "jt"));

        join.setType(columnReference.getForeignKeyColumn().isNullable() ? "LEFT" : "INNER");

        join.setLeft(columnReference.getPrimaryKeyColumn().getName());
        join.setRight(columnReference.getForeignKeyColumn().getName());

        join.setRightTable(root.getName());
        join.setRightTableAlias(root.getAlias());
        return join;
    }
}
