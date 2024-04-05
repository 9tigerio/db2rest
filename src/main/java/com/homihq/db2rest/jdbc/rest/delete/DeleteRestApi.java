package com.homihq.db2rest.jdbc.rest.delete;

import com.homihq.db2rest.core.dto.DeleteResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface DeleteRestApi {
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{tableName}")
    DeleteResponse delete(@PathVariable String tableName,
                          @RequestParam(name = "filter", required = false, defaultValue = "") String filter);
}
