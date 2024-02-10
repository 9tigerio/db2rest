package com.homihq.db2rest.rest.read.v2.processor.pre;

import com.homihq.db2rest.rest.read.v2.dto.ReadContextV2;

public interface ReadPreProcessor {

    void process(ReadContextV2 readContextV2);
}
