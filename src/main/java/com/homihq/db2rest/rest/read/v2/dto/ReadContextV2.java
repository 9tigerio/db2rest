package com.homihq.db2rest.rest.read.v2.dto;

import com.homihq.db2rest.mybatis.MyBatisTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SqlCriterion;
import org.mybatis.dynamic.sql.select.join.JoinSpecification;

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
    JoinSpecification joinSpecification;

    public void addWhereCondition(SqlCriterion whereCondition) {
        this.whereCondition = whereCondition;
    }

    public void addColumns(BasicColumn column) {
        columns.add(column);
    }
}
