package com.homihq.db2rest.jdbc.config.dto;

import lombok.Builder;

@Builder
public record UpdateResponse(int rows) {
}
