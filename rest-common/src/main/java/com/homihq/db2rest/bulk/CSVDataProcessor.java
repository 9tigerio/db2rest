package com.homihq.db2rest.bulk;


import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class CSVDataProcessor implements DataProcessor {

    private final CsvMapper csvMapper = new CsvMapper();

    @Override
    public List<Map<String, Object>> getData(InputStream inputStream) throws Exception {

        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        ObjectReader oReader = csvMapper.readerFor(Map.class).with(schema);

        List<Map<String, Object>> data = new ArrayList<>();
        try (Reader reader = new InputStreamReader(inputStream)) {
            MappingIterator<Map<String, String>> mi = oReader.readValues(reader);
            while (mi.hasNext()) {
                Map<String, String> current = mi.next();
                Map<String, Object> transformedRow = transform(current);
                data.add(transformedRow);

            }
        }

        return data;
    }

    @Override
    public boolean handle(String contentType) {
        return StringUtils.equalsIgnoreCase(contentType, "text/csv");
    }

    private Map<String, Object> transform(Map<String, String> data) {
        Map<String, Object> objectMap = new HashMap<>();

        objectMap.putAll(data);

        return objectMap;
    }

}
