package com.homihq.db2rest.jdbc.rest.sql;

import com.homihq.db2rest.jdbc.core.service.SQLTemplateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
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
	private final AntPathMatcher antPathMatcher;

	@GetMapping(VERSION + "/{dbId}/sql/{fileName}/**")
	public Object sqlTemplate(@PathVariable String dbId,
	                          @PathVariable String fileName,
	                          @RequestParam Map<String, String> requestParams,
	                          @RequestHeader Map<String, String> requestHeaders,
	                          @MatrixVariable Map<String, String> matrixVariables,
	                          HttpServletRequest request
	) throws IOException {
		Map<String, Object> context = getContext(requestParams, requestHeaders, matrixVariables, request);

		String renderedSqlQuery = sqlTemplateService.renderTemplate(fileName, context);

		return sqlTemplateService.execute(renderedSqlQuery, dbId);
	}

	private Map<String, Object> getContext(
			Map<String, String> requestParams,
			Map<String, String> requestHeaders,
			Map<String, String> matrixVariables,
			HttpServletRequest request
	) {
		Map<String, Object> context = new HashMap<>();
		context.put("params", requestParams);
		context.put("headers", requestHeaders);
		context.put("matrix", matrixVariables);

		var pathVariables = getContextFromPath(request);
		context.put("paths", pathVariables);

		return context;
	}

	private List<String> getContextFromPath(HttpServletRequest request) {
		String path = (String) request.getAttribute(
				HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

		String userPathVariable = antPathMatcher.extractPathWithinPattern(bestMatchPattern, path);

		return List.of(userPathVariable.split("/"));
	}
}
