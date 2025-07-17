package com.homihq.db2rest.jdbc.rest.create;

import com.homihq.db2rest.bulk.DataProcessor;
import com.homihq.db2rest.core.dto.CreateBulkResponse;
import com.homihq.db2rest.core.dto.CreateResponse;
import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.dtos.FileUploadContext;
import com.homihq.db2rest.jdbc.core.service.BulkCreateService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@Slf4j
@RequiredArgsConstructor
@EnableAsync
public class BulkCreateController implements BulkCreateRestApi {

    private final BulkCreateService bulkCreateService;
    private final List<DataProcessor> dataProcessors;


    public CreateBulkResponse save(
            String dbId,
            String tableName,
            String schemaName,
            List<String> includeColumns,
            List<String> sequences,
            boolean tsIdEnabled,
            HttpServletRequest request) throws Exception {

        DataProcessor dataProcessor = dataProcessors.stream()
                .filter(d -> d.handle(request.getContentType()))
                .findFirst().orElseThrow(() -> new GenericDataAccessException("Unable to process content type : "
                        + request.getContentType()));

        List<Map<String, Object>> data =
                dataProcessor.getData(request.getInputStream());


        return
                bulkCreateService.saveBulk(dbId, schemaName, tableName, includeColumns, data, tsIdEnabled, sequences);

    }

    @Override
    public CompletableFuture<CreateResponse> saveMultipartFile(
            String dbId,
            String tableName,
            String schemaName,
            List<String> includeColumns,
            List<String> sequences,
            boolean tsIdEnabled,
            MultipartFile file) {
        FileUploadContext context = new FileUploadContext(dbId, schemaName, tableName, includeColumns, tsIdEnabled, sequences, 0);
        return bulkCreateService.saveMultipartFile(context, file);
    }
}
