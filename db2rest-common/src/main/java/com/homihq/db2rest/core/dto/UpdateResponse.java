package com.homihq.db2rest.core.dto;

import lombok.Builder;

@Builder
public record UpdateResponse(int rows) {
}
