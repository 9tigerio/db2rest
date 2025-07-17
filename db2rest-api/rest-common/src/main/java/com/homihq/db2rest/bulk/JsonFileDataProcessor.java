package com.homihq.db2rest.bulk;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.homihq.db2rest.core.exception.GenericDataAccessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class JsonFileDataProcessor implements FileSubject {
    private static final int CHUNK_OBJECTS = 500;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JsonFactory jsonFactory = new JsonFactory();
    private JsonParser jsonParser;
    private FileStreamObserver observer;
    private boolean isInitialized = false;

    @Override
    public void register(FileStreamObserver observer) {
        this.observer = observer;
    }

    @Override
    public void unregister() {
        this.observer = null;
        if (jsonParser != null) {
            try {
                jsonParser.close();
            } catch (IOException e) {
                log.error("Error closing JsonParser: {}", e.getMessage(), e);
            } finally {
                jsonParser = null;
                isInitialized = false;
            }
        }
    }

    @Override
    public void startStreaming(InputStream inputStream) {
        try {
            initializeParser(inputStream);
            List<Map<String, Object>> dataChunk;
            while (inputStream.available() >= 0 && !((dataChunk = getData()).isEmpty())) {
                notifyObserver(dataChunk);
            }
        } catch (Exception e) {
            log.error("Error during JSON streaming: {}", e.getMessage(), e);
            throw new GenericDataAccessException(e.getMessage());
        } finally {
            unregister();
        }
    }

    private List<Map<String, Object>> getData() throws IOException {
        List<Map<String, Object>> result = new ArrayList<>();
        int count = 0;
        try {
            while (jsonParser.nextToken() != JsonToken.END_ARRAY && count < CHUNK_OBJECTS) {
                Map<String, Object> item = objectMapper.readValue(jsonParser, new TypeReference<>() {});
                result.add(item);
                count++;
            }
        } catch (MismatchedInputException e) {
            log.error("Error reading JSON data: {}", e.getLocalizedMessage());
        }
        return result;
    }

    private void notifyObserver(List<Map<String, Object>> dataChunk) {
        if (observer != null) {
            observer.update(dataChunk);
        } else {
            log.warn("No observer registered for file streaming.");
        }
    }

    private void initializeParser(InputStream inputStream) throws IOException {
        if (!isInitialized) {
            if (inputStream == null || inputStream.available() == 0) {
                throw new IllegalArgumentException("Input stream is empty or null");
            }
            jsonParser = jsonFactory.createParser(inputStream);

            if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
                throw new GenericDataAccessException("Expected a JSON array");
            }
            isInitialized = true;
        }
    }
}
