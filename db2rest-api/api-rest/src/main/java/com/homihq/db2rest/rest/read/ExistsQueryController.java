package com.homihq.db2rest.rest.read;

import com.homihq.db2rest.core.dto.ExistsResponse;
import com.homihq.db2rest.jdbc.core.service.ExistsQueryService;
import com.homihq.db2rest.jdbc.dto.JoinDetail;
import com.homihq.db2rest.jdbc.dto.ReadContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ExistsQueryController {

    private final ExistsQueryService existsQueryService;

    @GetMapping(value = VERSION + "/{dbId}/{tableName}/exists", produces = "application/json")
    public ExistsResponse exists(@PathVariable String dbId,
                                 @PathVariable String tableName,
                                 @RequestHeader(name = "Accept-Profile", required = false) String schemaName,
                                 @RequestParam(name = "filter", required = false, defaultValue = "") String filter) {

        log.debug("tableName - {}", tableName);
        log.debug("filter - {}", filter);

        ReadContext readContext = ReadContext.builder()
                .dbId(dbId)
                .schemaName(schemaName)
                .tableName(tableName)
                .filter(filter)
                .build();

        return existsQueryService.exists(readContext);
    }

	@PostMapping(value = VERSION + "/{dbId}/{tableName}/exists/_expand", produces = "application/json")
	public ExistsResponse exists(@PathVariable String dbId,
								@PathVariable String tableName,
								 @RequestHeader(name="Accept-Profile", required = false) String schemaName,
	                             @RequestParam(name = "filter", required = false, defaultValue = "") String filter,
	                             @RequestBody List<JoinDetail> joins
	) {

        ReadContext readContext = ReadContext.builder()
                .dbId(dbId)
                .schemaName(schemaName)
                .tableName(tableName)
                .fields("*")
                .filter(filter)
                .joins(joins)
                .build();

        return existsQueryService.exists(readContext);
    }
}
