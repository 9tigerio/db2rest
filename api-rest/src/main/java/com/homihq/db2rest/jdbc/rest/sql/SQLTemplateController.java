package com.homihq.db2rest.jdbc.rest.sql;

import com.homihq.db2rest.jdbc.core.service.SQLTemplateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Parameterized SQL Template  ", description = "Details about schemas and tables")
public class SQLTemplateController {
	private final SQLTemplateService sqlTemplateService;

	@GetMapping(VERSION + "/{dbId}/sql/{fileName}/{*userPathVariable}")
	public Object sqlTemplate(@PathVariable String dbId,
	                          @PathVariable String fileName,
	                          @PathVariable(name = "userPathVariable") String userPathVariable,
	                          @RequestParam Map<String, String> requestParams,
	                          @RequestHeader Map<String, String> requestHeaders,
	                          @MatrixVariable Map<String, String> matrixVariables
	) throws IOException {
		final Map<String, Object> context = getContext(userPathVariable, requestParams, requestHeaders, matrixVariables);
		final String renderedSqlQuery = sqlTemplateService.renderTemplate(fileName, context);
		return sqlTemplateService.execute(renderedSqlQuery, dbId);
	}

	private Map<String, Object> getContext(
			String userPathVariable,
			Map<String, String> requestParams,
			Map<String, String> requestHeaders,
			Map<String, String> matrixVariables
	) {
		final Map<String, Object> context = new HashMap<>();

		final List<String> userPathVariables = Arrays.stream(userPathVariable.split("/"))
				.filter(StringUtils::isNotEmpty)
				.toList();
		context.put("paths", userPathVariables);

		context.put("params", requestParams);
		context.put("headers", requestHeaders);
		context.put("matrix", matrixVariables);

		return context;
	}
}
