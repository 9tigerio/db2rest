package com.homihq.db2rest.rest.resource;


import com.homihq.db2rest.rest.service.DeleteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequiredArgsConstructor
public class DeleteResource {

    private final DeleteService deleteService;

    @DeleteMapping("/{tableName}")
    public void delete(@PathVariable String tableName
            ,@RequestParam(name = "rSql", required = false, defaultValue = "") String rSql) {

        deleteService.delete(tableName, rSql);
    }
}
