package com.homihq.db2rest.interceptor;

import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.multidb.DatabaseContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.Objects;


@Slf4j
@Component
public class DatabaseContextRequestInterceptor implements AsyncHandlerInterceptor {

       @Override
       public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
           log.debug("Pre handle");

           final Map<String, String> pathVariables = (Map<String, String>) request
                   .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

           if(Objects.isNull(pathVariables)) throw new GenericDataAccessException("Database ID not found.");

           String dbId = pathVariables.get("dbId");

           log.info("Db identifier : {}", dbId);

           if(StringUtils.isNotBlank(dbId)) {
               this.setTenantContext(dbId);
           }
           else{
               log.debug("DB could not be determined.");
               throw new GenericDataAccessException("Database ID not found.");
           }

           return true;
       }
 
       @Override
       public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
           DatabaseContextHolder.clear();
       }
         
       private void setTenantContext(String tenant) {
           DatabaseContextHolder.setCurrentDbId(tenant);
       }
}
