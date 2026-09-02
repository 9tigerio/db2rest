package com.homihq.db2rest.rest.delete;

import java.util.List;

import com.homihq.db2rest.auth.data.RoleDataFilter;
import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.config.MultiTenancy;
import com.homihq.db2rest.core.dto.DeleteResponse;
import com.homihq.db2rest.jdbc.core.service.DeleteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class DeleteController implements DeleteRestApi {

    private final DeleteService deleteService;
    private final Db2RestConfigProperties db2RestConfigProperties;

    @Override
    public DeleteResponse delete(
            Pair<List<RoleDataFilter>, String[]> roleBasedDataFilters,
            String dbId,
            String schemaName,
            String tableName,
            String filter) {

        db2RestConfigProperties.checkDeleteAllowed(filter);

        int rows = deleteService.delete(dbId, schemaName, tableName,
                MultiTenancy.joinFilters(filter, dbId, tableName, roleBasedDataFilters));
        log.debug("Number of rows deleted - {}", rows);
        return DeleteResponse.builder().rows(rows).build();
    }
}
