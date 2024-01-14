package com.homihq.db2rest.rest.create;

import com.homihq.db2rest.rest.create.dto.CreateBulkResponse;
import com.homihq.db2rest.rest.create.dto.CreateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CreateController {

    private final CreateService createService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping ("/{tableName}")
    public CreateResponse save(@PathVariable String tableName,
                               @RequestHeader(name = "Content-Profile") String schemaName,
                               @RequestBody Map<String,Object> data,
                               @RequestParam(name = "tsid", required = false) String tsid,
                               @RequestParam(name = "tsidType", required = false, defaultValue = "number") String tsidType) {

        int rows =
        createService.save(schemaName, tableName, data, tsid, tsidType);

        return new CreateResponse(rows);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping ( "/{tableName}/bulk")
    public CreateBulkResponse saveBulk(@PathVariable String tableName,
                                       @RequestHeader(name = "Content-Profile") String schemaName,
                                       @RequestBody List<Map<String,Object>> data) {

        int [] rows =
        createService.saveBulk(schemaName, tableName,data);

        return new CreateBulkResponse(rows);
    }
}
