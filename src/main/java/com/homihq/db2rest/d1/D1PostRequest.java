package com.homihq.db2rest.d1;

import java.util.List;

public record D1PostRequest(String sql, List<Object> params) {
}
