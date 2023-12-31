package com.homihq.db2rest.rest.query.model;

import lombok.Data;

@Data
public class RColumn {

    String table;
    String tableAlias;
    String name;
    String alias;
}
