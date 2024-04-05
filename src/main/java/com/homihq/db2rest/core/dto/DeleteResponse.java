package com.homihq.db2rest.core.dto;

import lombok.Builder;

@Builder
public record DeleteResponse(int rows) {
}
