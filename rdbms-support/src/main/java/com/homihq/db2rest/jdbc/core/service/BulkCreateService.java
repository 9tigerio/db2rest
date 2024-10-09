package com.homihq.db2rest.jdbc.core.service;

import com.homihq.db2rest.core.dto.CreateBulkResponse;
import com.homihq.db2rest.core.dto.CreateResponse;
import com.homihq.db2rest.dtos.FileUploadContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface BulkCreateService {
    CreateBulkResponse saveBulk(
            String dbId,
            String schemaName,
            String tableName,
            List<String> includedColumns,
            List<Map<String, Object>> dataList,
            boolean tsIdEnabled, List<String> sequences);

    @Async
    CompletableFuture<CreateResponse> saveMultipartFile(
            FileUploadContext fileUploadContext,
            MultipartFile file);
}
