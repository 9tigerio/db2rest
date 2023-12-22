package com.homihq.db2rest.schema;

import lombok.Data;

import java.util.List;

@Data
public class Fk {
    String name;


    List<FkField> fieldList;


}
