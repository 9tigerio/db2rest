package com.homihq.db2rest.rest.create.dto;

import java.util.List;
import java.util.Map;

public record CreateBulkResponse(int[] rows, Object keys) {
}
