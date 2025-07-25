package com.homihq.db2rest.rest.delete;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.core.dto.DeleteResponse;
import com.homihq.db2rest.jdbc.core.service.DeleteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class DeleteController implements DeleteRestApi {

    private final DeleteService deleteService;
    private final Db2RestConfigProperties db2RestConfigProperties;

    @Override
    public DeleteResponse delete(
            String dbId,
            String schemaName,
            String tableName,
            String filter
    ) {

        db2RestConfigProperties.checkDeleteAllowed(filter);

        int rows = deleteService.delete(dbId, schemaName, tableName, filter);
        log.debug("Number of rows deleted - {}", rows);
        return DeleteResponse.builder().rows(rows).build();
    }
}
