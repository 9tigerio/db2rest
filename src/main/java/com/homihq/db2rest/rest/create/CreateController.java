package com.homihq.db2rest.rest.create;


import com.homihq.db2rest.rest.create.dto.CreateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CreateController implements CreateRestApi {

    private final CreateService createService;

    @Override
    public CreateResponse save(String tableName,
                               String schemaName,
                               Map<String, Object> data,
                               String tsid,
                               String tsidType) {

        Pair<Integer, Object> result = createService.save(schemaName, tableName, data, tsid, tsidType);

        return new CreateResponse(result.getFirst(), result.getSecond());
    }

}
