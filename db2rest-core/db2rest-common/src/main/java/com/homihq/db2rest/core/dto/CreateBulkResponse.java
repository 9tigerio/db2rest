package com.homihq.db2rest.core.dto;

public record CreateBulkResponse(int[] rows, Object keys) {
}
