package com.homihq.db2rest.bulk;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.core.exception.BulkUploadException;

/**
 * Streams a JSON array of objects and hands the rows to a callback in chunks.
 * <p>
 * The processor keeps no per-upload state: the parser and the chunk buffer live
 * on the stack of {@link #stream}, so one instance can serve concurrent uploads.
 * The parser, and with it the input stream, is closed when {@code stream}
 * returns or throws, including when the callback throws.
 */
@Component
public class JsonFileDataProcessor {

    public static final int DEFAULT_CHUNK_SIZE = 500;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JavaType rowType = objectMapper.getTypeFactory()
            .constructMapType(Map.class, String.class, Object.class);

    /**
     * Reads {@code inputStream} as a JSON array of objects.
     *
     * @return the number of rows passed to the handler
     * @throws BulkUploadException if the input is empty, is not an array of objects,
     *                             contains no rows or is not valid JSON
     */
    public long stream(InputStream inputStream, int chunkSize, JsonChunkHandler handler) throws IOException {
        Objects.requireNonNull(inputStream, "inputStream");
        Objects.requireNonNull(handler, "handler");
        if (chunkSize < 1) {
            throw new IllegalArgumentException("chunkSize must be positive");
        }

        try (JsonParser parser = objectMapper.getFactory().createParser(inputStream)) {
            return readArray(parser, chunkSize, handler);
        } catch (JsonProcessingException e) {
            JsonLocation location = e.getLocation();
            long line = location == null ? -1 : location.getLineNr();
            long column = location == null ? -1 : location.getColumnNr();
            throw BulkUploadException.malformed(line, column, e);
        }
    }

    private long readArray(JsonParser parser, int chunkSize, JsonChunkHandler handler) throws IOException {
        JsonToken first = parser.nextToken();
        if (first == null) {
            throw BulkUploadException.emptyFile();
        }
        if (first != JsonToken.START_ARRAY) {
            throw BulkUploadException.notAnArray();
        }

        List<Map<String, Object>> chunk = new ArrayList<>(chunkSize);
        long index = 0;
        long chunkStart = 0;

        JsonToken token;
        while ((token = parser.nextToken()) != JsonToken.END_ARRAY) {
            if (token == null) {
                JsonLocation location = parser.currentLocation();
                throw BulkUploadException.malformed(location.getLineNr(), location.getColumnNr(), null);
            }
            if (token != JsonToken.START_OBJECT) {
                throw BulkUploadException.notAnObject(index);
            }
            Map<String, Object> row = objectMapper.readValue(parser, rowType);
            chunk.add(row);
            index++;

            if (chunk.size() == chunkSize) {
                handler.onChunk(chunk, chunkStart);
                chunk = new ArrayList<>(chunkSize);
                chunkStart = index;
            }
        }

        if (parser.nextToken() != null) {
            throw BulkUploadException.trailingContent();
        }
        if (index == 0) {
            throw BulkUploadException.noRows();
        }
        if (!chunk.isEmpty()) {
            handler.onChunk(chunk, chunkStart);
        }
        return index;
    }
}
