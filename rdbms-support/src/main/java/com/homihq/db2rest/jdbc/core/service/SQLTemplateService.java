package com.homihq.db2rest.jdbc.core.service;

import java.io.IOException;
import java.util.Map;

public interface SQLTemplateService {
	String renderTemplate(String template, Map<String, Object> context) throws IOException;

	Object execute(String sql, String dbId);
}
