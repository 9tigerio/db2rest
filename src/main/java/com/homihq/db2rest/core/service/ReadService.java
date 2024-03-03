package com.homihq.db2rest.core.service;

import com.homihq.db2rest.rest.read.dto.ReadContext;

public interface ReadService {
    Object findAll(ReadContext readContext);
}
