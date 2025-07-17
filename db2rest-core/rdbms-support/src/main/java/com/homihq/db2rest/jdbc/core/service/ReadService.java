package com.homihq.db2rest.jdbc.core.service;

import com.homihq.db2rest.jdbc.dto.ReadContext;

public interface ReadService {
    Object findAll(ReadContext readContext);
}
