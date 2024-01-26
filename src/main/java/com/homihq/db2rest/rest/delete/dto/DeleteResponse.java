package com.homihq.db2rest.rest.delete.dto;

import lombok.Builder;

@Builder
public record DeleteResponse(int rows) {
}
