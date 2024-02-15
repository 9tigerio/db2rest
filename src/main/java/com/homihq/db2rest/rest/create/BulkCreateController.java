package com.homihq.db2rest.rest.create;

import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.rest.create.bulk.DataProcessor;
import com.homihq.db2rest.rest.create.dto.CreateBulkResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class BulkCreateController implements BulkCreateRestApi {

    private final BulkCreateService bulkCreateService;

    private final List<DataProcessor> dataProcessors;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping (value= "/{tableName}/bulk",
            consumes = {"application/json", "text/csv"}
    )
    public CreateBulkResponse save(@PathVariable String tableName,
                            @RequestParam(name = "tsId", required = false) String tsId,
                            @RequestParam(name = "tsIdType", required = false, defaultValue = "number") String tsIdType,
                            HttpServletRequest request) throws Exception{

        DataProcessor dataProcessor = dataProcessors.stream()
                .filter(d -> d.handle(request.getContentType()))
                .findFirst().orElseThrow(() -> new GenericDataAccessException("Unable to process content type : "
                        + request.getContentType()));

        List<Map<String,Object>> data =
            dataProcessor.getData(request.getInputStream());


        Pair<int[], List<Map<String, Object>>> result =
                bulkCreateService.saveBulk(null, tableName, data, tsId, tsIdType);

        return new CreateBulkResponse(result.getFirst(), result.getSecond());
    }
}
