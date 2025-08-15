package com.homihq.db2rest.jdbc.core.service;

import com.homihq.db2rest.core.dto.ExistsResponse;
import com.homihq.db2rest.jdbc.dto.ReadContext;

public interface ExistsQueryService {
    ExistsResponse exists(ReadContext readContext);
}
