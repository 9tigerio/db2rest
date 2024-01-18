package com.homihq.db2rest.rest.create.bulk;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface DataProcessor {

    boolean handle(String contentType);

    List<Map<String,Object>> getData(InputStream inputStream) throws Exception;
}
