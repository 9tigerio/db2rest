package com.homihq.db2rest.rest.create;

import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.rest.create.bulk.DataProcessor;
import com.homihq.db2rest.rest.create.dto.CreateBulkResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class BulkCreateController implements BulkCreateApi {

    private final CreateService createService;

    private final List<DataProcessor> dataProcessors;

    @Override
    public CreateBulkResponse save(String tableName,
                                   String schemaName,
                                   String tsid,
                                   String tsidType,
                                   HttpServletRequest request) throws Exception{

        DataProcessor dataProcessor = dataProcessors.stream()
                .filter(d -> d.handle(request.getContentType()))
                .findFirst().orElseThrow(() -> new GenericDataAccessException("Unable to process content type : "
                        + request.getContentType()));

        List<Map<String,Object>> data =
            dataProcessor.getData(request.getInputStream());

        log.info("### data -> {}", data);

        int [] rows =
                createService.saveBulk(schemaName, tableName, data, tsid, tsidType);

        return new CreateBulkResponse(rows);
    }
}
