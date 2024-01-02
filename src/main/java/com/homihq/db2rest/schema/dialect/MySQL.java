package com.homihq.db2rest.schema.dialect;

import java.util.List;

public class MySQL {

    public List<String> getSystemSchemas() {
        return List.of("sys");
    }
}
