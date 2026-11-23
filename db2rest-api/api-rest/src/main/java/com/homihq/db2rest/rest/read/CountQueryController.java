package com.homihq.db2rest.rest.read;

import com.homihq.db2rest.auth.data.RoleFilterContext;
import com.homihq.db2rest.config.MultiTenancy;
import com.homihq.db2rest.core.dto.CountResponse;
import com.homihq.db2rest.jdbc.core.service.CountQueryService;
import com.homihq.db2rest.jdbc.dto.ReadContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.homihq.db2rest.auth.data.RoleFilterContext.REQUEST_ATTRIBUTE;
import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;

import java.util.List;

/**
 * Counts the rows of a table, as {@code SELECT COUNT(*)}, that match the optional RSQL
 * {@code filter}.
 * <p>
 * When auth is enabled and the table has role data filters, the user's tenant conditions are
 * ANDed onto the request filter (see {@link MultiTenancy#joinFilters}), so the count only
 * includes rows the same user could read through the read endpoints. A user with no matching
 * tenant role only counts rows where the tenant column is null.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class CountQueryController {

    private final CountQueryService countQueryService;

    @GetMapping(VERSION + "/{dbId}/{tableName}/count")
    public CountResponse count(
            @RequestAttribute(name = REQUEST_ATTRIBUTE, required = false) RoleFilterContext roleFilterContext,
            @PathVariable String dbId,
            @PathVariable String tableName,
            @RequestHeader(name = "Accept-Profile", required = false) String schemaName,
            @RequestParam(name = "filter", required = false, defaultValue = "") String filter) {

        log.debug("tableName - {}", tableName);
        log.debug("filter - {}", filter);

        ReadContext readContext = ReadContext.builder()
                .dbId(dbId)
                .schemaName(schemaName)
                .tableName(tableName)
                .filter(MultiTenancy.joinFilters(filter, dbId, tableName, roleFilterContext))
                .build();

        return countQueryService.count(readContext);
    }
}
