package com.homihq.db2rest.bulk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class JSONDataProcessor implements DataProcessor{

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public List<Map<String, Object>> getData(InputStream inputStream) throws Exception{
        return objectMapper.readValue(inputStream, new TypeReference<List<Map<String, Object>>>(){});
    }

    @Override
    public boolean handle(String contentType) {
        return StringUtils.equalsIgnoreCase(contentType, "application/json");
    }
}
