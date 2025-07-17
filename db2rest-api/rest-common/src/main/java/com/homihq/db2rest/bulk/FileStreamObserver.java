package com.homihq.db2rest.bulk;

import java.util.List;
import java.util.Map;

public interface FileStreamObserver {
    void update(List<Map<String, Object>> data);
}
