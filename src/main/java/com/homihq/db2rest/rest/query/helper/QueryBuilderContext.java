package com.homihq.db2rest.rest.query.helper;

import com.homihq.db2rest.rest.query.model.RColumn;
import com.homihq.db2rest.rest.query.model.RTable;
import lombok.Data;

import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class QueryBuilderContext {

    private SelectJoinStep<Record> selectJoinStep;
    String schemaName;
    String tableName;

    String select;

    String filter;

    String joinTable;

    Pageable pageable;



    private String qSelect; //Holds select col1, col2 or *
    public String getQualifiedTableName() {
        return schemaName + "." + tableName;
    }

    public void buildAstrix() {
        this.qSelect = "SELECT " + this.tableName + ".* ";
        System.out.println("qSelect -> " + qSelect);
    }

    public void buildSelectColumns(List<RTable> rTables) {
        List<RColumn> columnList = new ArrayList<>();

        for(RTable table : rTables) {
            columnList.addAll(table.getColumns());
        }

        List<String> cols = columnList.stream()
                .map(i -> i.getTable() + "." + i.getName()).toList();

        this.qSelect = "SELECT " + String.join(",", cols);

        System.out.println("qSelect -> " + qSelect);
    }

}
