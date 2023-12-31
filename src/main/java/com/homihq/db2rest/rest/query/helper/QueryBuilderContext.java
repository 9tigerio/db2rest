package com.homihq.db2rest.rest.query.helper;

import com.homihq.db2rest.rest.query.model.RColumn;
import com.homihq.db2rest.rest.query.model.RJoin;
import com.homihq.db2rest.rest.query.model.RTable;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
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
    List<RColumn> rColumns;

    private String qSelect; //Holds select col1, col2 or *
    private String qJoin;

    private boolean astrix;

    public String getQualifiedTableName() {
        return schemaName + "." + tableName;
    }

    public String getRootTable() {
        return
        this.rTables.stream()
                .filter(t -> StringUtils.equalsIgnoreCase(t.getName(), this.tableName))
                .map(t -> tableName + " " + t.getAlias())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Root table not found."));

    }

    public void buildAstrix() {
        this.qSelect = "SELECT " + this.tableName + ".*" + " FROM " + getQualifiedTableName();
        System.out.println("qSelect -> " + qSelect);
    }


    public void buildSelectColumns() {
        List<String> cols =
                rColumns.stream()
                        .map(i ->
                                {
                                    if(StringUtils.isNotBlank(i.getAlias())) {
                                        return i.getTableAlias() + "." + i.getName() + " as " + i.getAlias();
                                    }
                                    else{
                                        return i.getTableAlias() + "." + i.getName();
                                    }
                                }

                        ).toList();


        this.qSelect = "SELECT " + String.join(" , ", cols) + " FROM " + getRootTable();

        System.out.println("qSelect -> " + qSelect);
    }

    public void buildJoin() {

        List<String> joins = rJoins.stream()
                .map(RJoin::getJoin)
                .toList();
        this.qJoin = String.join(" ", joins);

        System.out.println("qSelect -> " + qJoin);
    }

    public void buildSQL() {
        buildSelectColumns();
        buildJoin();

    }

}
