package com.homihq.db2rest.core.service;

import com.homihq.db2rest.rest.read.dto.ExistsResponse;
import com.homihq.db2rest.rest.read.dto.ReadContext;

public interface ExistsQueryService {
    ExistsResponse exists(ReadContext readContext);
}
