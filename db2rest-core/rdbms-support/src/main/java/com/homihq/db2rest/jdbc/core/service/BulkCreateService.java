package com.homihq.db2rest.jdbc.core.service;

import com.homihq.db2rest.core.dto.CreateBulkResponse;
import com.homihq.db2rest.core.dto.CreateResponse;
import com.homihq.db2rest.dtos.BulkContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface BulkCreateService {
    CreateBulkResponse saveBulk(
            BulkContext bulkContext,
            List<Map<String, Object>> dataList);

    /**
     * Imports a JSON array file into one table. All rows are committed together or
     * none are. The returned future is complete when this method returns.
     */
    CompletableFuture<CreateResponse> saveMultipartFile(
            BulkContext bulkContext,
            MultipartFile file);
}
