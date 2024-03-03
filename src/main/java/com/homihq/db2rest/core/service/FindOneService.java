package com.homihq.db2rest.core.service;

import com.homihq.db2rest.rest.read.dto.ReadContext;

import java.util.Map;

public interface FindOneService {
    Map<String, Object> findOne(ReadContext readContext);
}
