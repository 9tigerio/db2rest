package com.homihq.db2rest.jdbc.config.core.service;

import com.homihq.db2rest.core.dto.ExistsResponse;
import com.homihq.db2rest.jdbc.config.dto.ReadContext;

public interface ExistsQueryService {
    ExistsResponse exists(ReadContext readContext);
}
