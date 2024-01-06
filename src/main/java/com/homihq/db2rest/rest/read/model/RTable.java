package com.homihq.db2rest.rest.read.model;

import lombok.Data;

import java.util.List;

@Data
public class RTable {

    String schema;
    String name;
    String alias;

    List<RColumn> columns;
}
