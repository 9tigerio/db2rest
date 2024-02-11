package com.homihq.db2rest.rest.read.dto;

import com.homihq.db2rest.mybatis.MyBatisTable;
import com.homihq.db2rest.rest.read.dto.JoinDetail;
import com.homihq.db2rest.rest.read.model.DbColumn;
import com.homihq.db2rest.rest.read.model.DbTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SqlCriterion;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class ReadContextV2 {

    /* Input Attributes */
    String tableName;
    String fields;
    String filter;
    List<String> sorts;
    int limit;
    long offset;
    List<JoinDetail> joins;


    /* Processed attributes */
    MyBatisTable rootTable;
    List<BasicColumn> columns;
    SqlCriterion whereCondition;


    /* Attributes to replace the ones above */
    DbTable root;
    List<DbColumn> cols;



    public void addWhereCondition(SqlCriterion whereCondition) {
        this.whereCondition = whereCondition;
    }



    public void addColumns(List<BasicColumn> columnList) {
        this.columns.addAll(columnList);
    }
}
