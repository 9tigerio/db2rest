package com.homihq.db2rest.jdbc.rest.create;

import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.jdbc.core.service.BulkCreateService;
import com.homihq.db2rest.bulk.DataProcessor;
import com.homihq.db2rest.core.dto.CreateBulkResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class BulkCreateController implements BulkCreateRestApi {

    private final BulkCreateService bulkCreateService;
    private final List<DataProcessor> dataProcessors;


    public CreateBulkResponse save(
            String dbId,
            String tableName,
            String schemaName,
                                   List<String> includeColumns,
                                   List<String> sequences,
                                   boolean tsIdEnabled,
                                   HttpServletRequest request) throws Exception{

        DataProcessor dataProcessor = dataProcessors.stream()
                .filter(d -> d.handle(request.getContentType()))
                .findFirst().orElseThrow(() -> new GenericDataAccessException("Unable to process content type : "
                        + request.getContentType()));

        List<Map<String,Object>> data =
            dataProcessor.getData(request.getInputStream());



        return
                bulkCreateService.saveBulk(dbId, schemaName, tableName, includeColumns, data, tsIdEnabled, sequences);

    }
}
