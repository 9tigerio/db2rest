package com.homihq.db2rest.rest.read.processor.pre;

import com.homihq.db2rest.rest.read.dto.ReadContextV2;

public interface ReadProcessor {

    void process(ReadContextV2 readContextV2);
}
