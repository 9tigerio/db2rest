package com.homihq.db2rest.jdbc.config.core.service;

import com.homihq.db2rest.jdbc.config.dto.ReadContext;

import java.util.Map;

public interface FindOneService {
    Map<String, Object> findOne(ReadContext readContext);
}
