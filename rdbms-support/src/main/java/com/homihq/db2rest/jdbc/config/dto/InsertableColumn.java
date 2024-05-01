package com.homihq.db2rest.jdbc.config.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InsertableColumn {

    String columnName;
    String sequence;
}
