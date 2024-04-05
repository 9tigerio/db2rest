package com.homihq.db2rest.core.service;

import com.homihq.db2rest.jdbc.rest.read.dto.CountResponse;
import com.homihq.db2rest.jdbc.rest.read.dto.ReadContext;

public interface CountQueryService {
    CountResponse count(ReadContext readContext);
}
