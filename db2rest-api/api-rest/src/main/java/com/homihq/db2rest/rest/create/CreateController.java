package com.homihq.db2rest.rest.create;


import com.homihq.db2rest.core.dto.CreateResponse;
import com.homihq.db2rest.jdbc.core.service.CreateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CreateController implements CreateRestApi {

    private final CreateService createService;

    @Override
    public CreateResponse save(
            String dbId, String schemaName,
            String tableName,
            List<String> includeColumns,
            List<String> sequences,
            Map<String, Object> data,
            boolean tsIdEnabled
    ) {

        return createService
                .save(dbId, schemaName, tableName, includeColumns, data, tsIdEnabled, sequences);

    }

}
