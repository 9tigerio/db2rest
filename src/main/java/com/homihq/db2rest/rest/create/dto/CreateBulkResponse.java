package com.homihq.db2rest.rest.create.dto;

import java.util.List;

public record CreateBulkResponse(int[] rows, List<Object> generated_keys) {
}
