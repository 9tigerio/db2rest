package com.homihq.db2rest.rest.query.helper;

import com.homihq.db2rest.rest.query.model.RJoin;
import com.homihq.db2rest.rest.query.model.RTable;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Data
public class QueryBuilderContext {


    String schemaName;
    String tableName;
    String select;
    String filter;


    Pageable pageable;

    List<RTable> rTables;
    List<RJoin> rJoins;

    private String qSelect; //Holds select col1, col2 or *
    private String qJoin;

    public String getQualifiedTableName() {
        return schemaName + "." + tableName;
    }

    public void buildAstrix() {
        this.qSelect = "SELECT " + this.tableName + ".*" + " FROM " + getQualifiedTableName();
        System.out.println("qSelect -> " + qSelect);
    }

    public void buildSelectColumns(List<RTable> rTables) {
        this.rTables = rTables;
        List<String> cols =
                rTables.stream()
                        .flatMap(i -> i.getColumns().stream())
                        .toList()
                        .stream()
                        .map(i -> i.getTable() + "." + i.getName()).toList();


        this.qSelect = "SELECT " + String.join(" , ", cols) + " FROM " + getQualifiedTableName();

        System.out.println("qSelect -> " + qSelect);
    }

    public void buildJoin(List<RJoin> rJoins) {
        this.rJoins = rJoins;
        List<String> joins = rJoins.stream()
                .map(RJoin::getJoin)
                .toList();
        this.qJoin = String.join(" ", joins);

        System.out.println("qSelect -> " + qJoin);
    }

}
