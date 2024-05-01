package com.homihq.db2rest.jdbc.rest.read;

import com.homihq.db2rest.jdbc.config.core.service.ExistsQueryService;
import com.homihq.db2rest.jdbc.config.dto.ExistsResponse;
import com.homihq.db2rest.jdbc.config.dto.JoinDetail;
import com.homihq.db2rest.jdbc.config.dto.ReadContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ExistsQueryController {

	private final ExistsQueryService existsQueryService;

	@GetMapping(value = "/{tableName}/exists", produces = "application/json")
	public ExistsResponse exists(@PathVariable String tableName,
                                 @RequestHeader(name="Accept-Profile", required = false) String schemaName,
                                 @RequestParam(name = "filter", required = false, defaultValue = "") String filter) {

		log.debug("tableName - {}", tableName);
		log.debug("filter - {}", filter);

		ReadContext readContext = ReadContext.builder()
				.schemaName(schemaName)
				.tableName(tableName)
				.filter(filter)
				.build();

		return existsQueryService.exists(readContext);
	}

	@PostMapping(value = "/{tableName}/exists/_expand", produces = "application/json")
	public ExistsResponse exists(@PathVariable String tableName,
	                             @RequestParam(name = "filter", required = false, defaultValue = "") String filter,
	                             @RequestBody List<JoinDetail> joins
	) {

		ReadContext readContext = ReadContext.builder()
				.tableName(tableName)
				.fields("*")
				.filter(filter)
				.joins(joins)
				.build();

		return existsQueryService.exists(readContext);
	}
}
