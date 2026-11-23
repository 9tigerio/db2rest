package com.homihq.db2rest.rest.create;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

import com.homihq.db2rest.auth.data.RoleFilterContext;
import com.homihq.db2rest.config.MultiTenancy;
import com.homihq.db2rest.core.dto.CreateResponse;
import com.homihq.db2rest.jdbc.core.service.CreateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CreateController implements CreateRestApi {

    private final CreateService createService;

    @Override
    public CreateResponse save(
            RoleFilterContext roleFilterContext,
            String dbId, String schemaName,
            String tableName,
            List<String> includeColumns,
            List<String> sequences,
            Map<String, Object> data,
            boolean tsIdEnabled) {

        MultiTenancy.addTenantColumns(data, dbId, tableName, roleFilterContext);
        return createService.save(dbId, schemaName, tableName, includeColumns, data, tsIdEnabled, sequences);
    }
}
