package com.homihq.db2rest.jdbc.rest.sql;

import com.homihq.db2rest.core.exception.PathVariableNamesMissingException;
import com.homihq.db2rest.core.exception.PathVariableValuesMissingException;
import com.homihq.db2rest.jdbc.core.service.SQLTemplateExecutorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private final SQLTemplateExecutorService sqlTemplateExecutorService;

    @GetMapping({
            VERSION + "/{dbId}/sql/{fileName}/{*userPathVariable}"
    })
    public Object sqlTemplate(@PathVariable String dbId,
                              @PathVariable String fileName,
                              @PathVariable(name = "userPathVariable") String userPathVariable,
                              @RequestParam Map<String, String> requestParams,
                              @RequestHeader Map<String, String> requestHeaders,
                              @MatrixVariable Map<String, String> matrixVariables
    ) {
        final Map<String, Object> context = createContext(userPathVariable, requestParams, requestHeaders, matrixVariables);

        log.info("context - {}", context);

        return sqlTemplateExecutorService.execute(dbId, fileName, context);

    }

    private Map<String, Object> createContext(
            String userPathVariable,
            Map<String, String> requestParams,
            Map<String, String> requestHeaders,
            Map<String, String> matrixVariables
    ) {
        final Map<String, Object> context = new HashMap<>();

        final List<String> userPathVariables = Arrays.stream(userPathVariable.split("/"))
                .filter(StringUtils::isNotEmpty)
                .toList();

        final String headerPaths = requestHeaders.get("paths");

        if (!userPathVariables.isEmpty() && StringUtils.isBlank(headerPaths)) {
            throw new PathVariableNamesMissingException();
        }

        Map<String, String> pathVariables = new HashMap<>();

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

        context.put("paths", pathVariables);
        context.put("params", requestParams);
        context.put("headers", requestHeaders);
        context.put("matrix", matrixVariables);

        return context;
    }
}
