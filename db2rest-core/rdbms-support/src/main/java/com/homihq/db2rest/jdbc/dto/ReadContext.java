package com.homihq.db2rest.jdbc.dto;


import com.db2rest.jdbc.dialect.model.DbColumn;
import com.db2rest.jdbc.dialect.model.DbJoin;
import com.db2rest.jdbc.dialect.model.DbSort;
import com.db2rest.jdbc.dialect.model.DbTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class ReadContext {

    /* Input Attributes */
    String dbId;
    String schemaName;
    String tableName;
    String fields;
    String filter;
    List<String> sorts;
    int limit;
    long offset;
    List<JoinDetail> joins;
    int defaultFetchLimit;


    /* Derived attributes */
    DbTable root;
    List<DbColumn> cols;
    String rootWhere;
    Map<String, Object> paramMap;
    List<DbJoin> dbJoins;
    List<DbSort> dbSortList;
    List<DbTable> allTables; // All tables involved in the query (root + joined tables)

    public void createParamMap() {
        if (Objects.isNull(paramMap)) {
            paramMap = new HashMap<>();
        }
    }

    public void addColumns(List<DbColumn> columnList) {
        this.cols.addAll(columnList);
    }

    public void addJoin(DbJoin join) {
        if (Objects.isNull(dbJoins))
            dbJoins = new ArrayList<>();
        dbJoins.add(join);
    }

    public void addTable(DbTable table) {
        if (Objects.isNull(allTables))
            allTables = new ArrayList<>();
        allTables.add(table);
    }
}
