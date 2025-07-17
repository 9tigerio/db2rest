package com.homihq.db2rest.jdbc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InsertableColumn {

    String columnName;
    String sequence;
}
