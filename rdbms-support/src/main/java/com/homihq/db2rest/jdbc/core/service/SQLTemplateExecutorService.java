package com.homihq.db2rest.jdbc.core.service;


import java.util.Map;

public interface SQLTemplateExecutorService {

    Object execute(String dbId, String templateFile, Map<String, Object> context);

}
