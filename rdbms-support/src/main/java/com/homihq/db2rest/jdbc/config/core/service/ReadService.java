package com.homihq.db2rest.jdbc.config.core.service;

import com.homihq.db2rest.jdbc.config.dto.ReadContext;

public interface ReadService {
    Object findAll(ReadContext readContext);
}
