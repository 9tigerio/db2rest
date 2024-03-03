package com.homihq.db2rest.rest.read.dto;


import com.homihq.db2rest.core.model.DbColumn;
import com.homihq.db2rest.core.model.DbJoin;
import com.homihq.db2rest.core.model.DbSort;
import com.homihq.db2rest.core.model.DbTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import java.util.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class ReadContext {

    /* Input Attributes */
    String tableName;
    String fields;
    String filter;
    List<String> sorts;
    int limit;
    long offset;
    List<JoinDetail> joins;


    /* Derived attributes */
    DbTable root;
    List<DbColumn> cols;
    String rootWhere;
    Map<String,Object> paramMap;
    List<DbJoin> dbJoins;
    List<DbSort> dbSortList;

    public void createParamMap() {
        if(Objects.isNull(paramMap)) {
            paramMap = new HashMap<>();
        }
    }

    public void addColumns(List<DbColumn> columnList) {
        this.cols.addAll(columnList);
    }

    public void addJoin(DbJoin join) {
        if(Objects.isNull(dbJoins)) dbJoins = new ArrayList<>();

        dbJoins.add(join);
    }
}
