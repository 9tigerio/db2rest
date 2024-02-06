package com.homihq.db2rest.rest.read.v2.processor;

import com.homihq.db2rest.rest.read.v2.dto.ReadContextV2;

public interface ReadProcessor {

    void process(ReadContextV2 readContextV2);
}
