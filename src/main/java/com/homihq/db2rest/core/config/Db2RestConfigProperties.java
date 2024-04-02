package com.homihq.db2rest.core.config;

import com.homihq.db2rest.exception.DeleteOpNotAllowedException;
import com.homihq.db2rest.exception.InvalidSchemaException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "db2rest")
@Validated
@Slf4j
public class Db2RestConfigProperties {

    private boolean allowSafeDelete;
    private int defaultFetchLimit;

    private List<String> includeSchemas;

    private MultiTenancy multiTenancy;


    public boolean isAllSchema() {

        if(Objects.isNull(includeSchemas)) {
            return true;
        }
        else{
            return includeSchemas.size() == 1 && includeSchemas.get(0).equals("#{null}");
        }

    }


    public void checkDeleteAllowed(String filter) {
        if (StringUtils.isBlank(filter) && allowSafeDelete)
            throw new DeleteOpNotAllowedException(true);
    }


}
