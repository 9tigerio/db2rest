package com.homihq.db2rest.jdbc.config.core.service;

import com.homihq.db2rest.core.dto.CountResponse;
import com.homihq.db2rest.jdbc.config.dto.ReadContext;

public interface CountQueryService {
    CountResponse count(ReadContext readContext);
}
