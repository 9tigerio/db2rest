package com.homihq.db2rest.interceptor;

import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.multidb.DatabaseContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;

@Slf4j
@Component
public class DatabaseContextRequestInterceptor implements AsyncHandlerInterceptor {

    private final List<String> whiteList = List.of(
            "/swagger-ui/**", "/v3/api-docs/**", "/actuator/**", "/admin/**",
            VERSION + "/$dbs");

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("Pre handle - {}", request.getRequestURI());


        if (!isWhileListed(request.getRequestURI())) {
            final Map<String, String> pathVariables = (Map<String, String>) request
                    .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

            log.info("pathVariables - {}", pathVariables);

            if (Objects.isNull(pathVariables)) {
                throw new GenericDataAccessException("Database ID not found.");
            }

            String dbId = pathVariables.get("dbId");

            log.debug("Db identifier : {}", dbId);

            if (StringUtils.isNotBlank(dbId)) {
                this.setTenantContext(dbId);
            } else {
                log.info("DB could not be determined.");
                throw new GenericDataAccessException("Database ID not found.");
            }
        }


        return true;
    }

    private boolean isWhileListed(String uri) {
        return whiteList.stream().anyMatch(w -> antPathMatcher.match(w, uri));
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        DatabaseContextHolder.clear();
    }

    private void setTenantContext(String tenant) {
        DatabaseContextHolder.setCurrentDbId(tenant);
    }
}
