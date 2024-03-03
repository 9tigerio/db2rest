package com.homihq.db2rest.core.service;

import com.homihq.db2rest.rest.read.dto.CountResponse;
import com.homihq.db2rest.rest.read.dto.ReadContext;

public interface CountQueryService {
    CountResponse count(ReadContext readContext);
}
