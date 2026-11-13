package com.homihq.db2rest.rest.update;

import com.homihq.db2rest.auth.data.RoleDataFilter;
import com.homihq.db2rest.config.MultiTenancy;
import com.homihq.db2rest.core.dto.UpdateResponse;
import com.homihq.db2rest.jdbc.core.service.UpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.homihq.db2rest.config.MultiTenancy.ROLEBASEDDATAFILTERS;
import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UpdateController {

    private final UpdateService updateService;

    @PatchMapping(VERSION + "/{dbId}/{tableName}")
    public UpdateResponse save(
            @RequestAttribute(name = ROLEBASEDDATAFILTERS, required = false) Pair<List<RoleDataFilter>, String[]> roleBasedDataFilters,
            @PathVariable String dbId,
            @PathVariable String tableName,
            @RequestHeader(name = "Content-Profile", required = false) String schemaName,
            @RequestBody Map<String, Object> data,
            @RequestParam(name = "filter", required = false, defaultValue = "") String filter) {

        MultiTenancy.addTenantColumns(data, dbId, tableName, roleBasedDataFilters);
        int rows = updateService.patch(dbId, schemaName, tableName, data,
                MultiTenancy.joinFilters(filter, dbId, tableName, roleBasedDataFilters));
        return new UpdateResponse(rows);
    }
}
