package com.homihq.db2rest.jdbc.core.service;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.jdbc.JdbcManager;
import com.homihq.db2rest.jdbc.core.DbOperationService;
import com.hubspot.jinjava.Jinjava;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JinJavaTemplateService implements SQLTemplateService {

	private final Jinjava jinjava;
	private final Db2RestConfigProperties db2RestConfigProperties;
	private final DbOperationService dbOperationService;
	private final JdbcManager jdbcManager;

	@Override
	public String renderTemplate(String templateName, Map<String, Object> context) throws IOException {
		log.debug("Rendering query from template {}", templateName);
		String userTemplateLocation = db2RestConfigProperties.getTemplates();
		Path templatePath = Paths.get(userTemplateLocation, templateName + ".sql");
		if (!Files.exists(templatePath)) {
			throw new IllegalArgumentException("Template not found: " + templateName);
		}
		try {
			String templateContent = Files.readString(templatePath);
			return jinjava.render(templateContent, context);
		} catch (IOException ioe) {
			throw new IOException("Could not read SQL template at: " + templatePath);
		}
	}

	@Override
	public Object execute(String sql, String dbId) {
		log.debug("Execute: {}", sql);
		return dbOperationService.query(
				jdbcManager.getNamedParameterJdbcTemplate(dbId),
				sql,
				jdbcManager.getDialect(dbId)
		);
	}
}
