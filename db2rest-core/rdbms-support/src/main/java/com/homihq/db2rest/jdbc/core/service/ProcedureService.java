package com.homihq.db2rest.jdbc.core.service;


import java.util.List;
import java.util.Map;

public interface ProcedureService {

    Map<String, Object> execute(String dbId, String subRoutineName, Map<String, Object> inParams, List<String> resultSetKeys);
}
