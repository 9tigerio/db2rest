package com.homihq.db2rest.jdbc.config.processor;

import com.homihq.db2rest.jdbc.config.dto.ReadContext;

public interface ReadProcessor {

    void process(ReadContext readContext);
}
