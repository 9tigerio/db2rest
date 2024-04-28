package com.homihq.db2rest.core.bulk;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface DataProcessor {

    boolean handle(String contentType);

    List<Map<String,Object>> getData(InputStream inputStream) throws Exception;


}
