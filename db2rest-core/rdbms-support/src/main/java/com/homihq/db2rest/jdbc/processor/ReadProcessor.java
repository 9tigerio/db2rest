package com.homihq.db2rest.jdbc.processor;

import com.homihq.db2rest.jdbc.dto.ReadContext;

public interface ReadProcessor {

    void process(ReadContext readContext);
}
