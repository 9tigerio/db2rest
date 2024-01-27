package com.homihq.db2rest.rest.delete;


import com.homihq.db2rest.rest.delete.dto.DeleteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequiredArgsConstructor
public class DeleteController {

    private final DeleteService deleteService;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{tableName}")
    public DeleteResponse delete(@PathVariable String tableName,
                                 @RequestParam(name = "schemaName", required = false) String schemaName,
                                 @RequestParam(name = "filter", required = false, defaultValue = "") String filter) {

        int rows = deleteService.delete(schemaName, tableName, filter);
        log.debug("Number of rows deleted - {}", rows);
        return DeleteResponse.builder().rows(rows).build();
    }
}
