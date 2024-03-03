package com.homihq.db2rest.rest.read;

import com.homihq.db2rest.core.service.ExistsQueryService;
import com.homihq.db2rest.rest.read.dto.ExistsResponse;
import com.homihq.db2rest.rest.read.dto.JoinDetail;
import com.homihq.db2rest.rest.read.dto.ReadContext;
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
	                             @RequestParam(name = "filter", required = false, defaultValue = "") String filter) {

		log.info("tableName - {}", tableName);
		log.info("filter - {}", filter);

		ReadContext readContext = ReadContext.builder()
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
