package com.homihq.db2rest.rest.sql;

import com.homihq.db2rest.core.exception.PathVariableNamesMissingException;
import com.homihq.db2rest.core.exception.PathVariableValuesMissingException;
import com.homihq.db2rest.jdbc.core.service.SQLTemplateExecutorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;

@Slf4j
@RestController
@RequestMapping(VERSION + "/{dbId}/sql")
@RequiredArgsConstructor
@Tag(name = "Parameterized SQL Template  ", description = "Details about schemas and tables")
public class SQLTemplateController {
    private final SQLTemplateExecutorService sqlTemplateExecutorService;

    @GetMapping("/{fileName}/{*userPathVariable}")
    public Object sqlTemplate(@PathVariable String dbId,
                              @PathVariable String fileName,
                              @PathVariable(name = "userPathVariable") String userPathVariable,
                              @RequestParam Map<String, String> requestParams,
                              @RequestHeader Map<String, String> requestHeaders,
                              @MatrixVariable Map<String, String> matrixVariables
    ) {
        final Map<String, Object> context = createContext(requestParams, requestHeaders, matrixVariables);
        Map<String, Object> paths = createPaths(userPathVariable, requestHeaders);
        context.put("paths", paths);

        log.debug("context - {}", context);

        return sqlTemplateExecutorService.execute(dbId, fileName, context);
    }

    @PostMapping("/{fileName}")
    public Object sqlTemplate(@PathVariable String dbId,
                                @PathVariable String fileName,
                                @RequestBody Map<String, Object> content,
                                @RequestParam Map<String, String> requestParams,
                                @RequestHeader Map<String, String> requestHeaders,
                                @MatrixVariable Map<String, String> matrixVariables) {

        final Map<String, Object> context = createContext(requestParams, requestHeaders, matrixVariables);
        context.put("content", content);

        log.debug("context - {}", context);

        return sqlTemplateExecutorService.execute(dbId, fileName, context);
    }

    private Map<String, Object> createContext(
            Map<String, String> requestParams,
            Map<String, String> requestHeaders,
            Map<String, String> matrixVariables
    ) {
        final Map<String, Object> context = new HashMap<>();
        context.put("params", requestParams);
        context.put("headers", requestHeaders);
        context.put("matrix", matrixVariables);
        return context;
    }

    private static Map<String, Object> createPaths(String userPathVariable, Map<String, String> requestHeaders) {
        final List<String> userPathVariables = Arrays.stream(userPathVariable.split("/"))
                .filter(StringUtils::isNotEmpty)
                .toList();

        final String headerPaths = requestHeaders.get("paths");

        if (!userPathVariables.isEmpty() && StringUtils.isBlank(headerPaths)) {
            throw new PathVariableNamesMissingException();
        }

        Map<String, Object> pathVariables = new HashMap<>();
        if (StringUtils.isNotBlank(headerPaths)) {
            String[] pathKeys = headerPaths.split(",");

            if (pathKeys.length != userPathVariables.size()) {
                throw new PathVariableValuesMissingException();
            }

            for (int i = 0; i < pathKeys.length; i++) {
                final String key = pathKeys[i];
                final String pathValue = userPathVariables.get(i);
                if (StringUtils.isBlank(pathValue)) {
                    throw new PathVariableValuesMissingException(key);
                }
                pathVariables.put(key, pathValue);
            }
        }
        return pathVariables;
    }
}
