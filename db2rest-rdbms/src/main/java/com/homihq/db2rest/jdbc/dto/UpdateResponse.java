package com.homihq.db2rest.jdbc.dto;

import lombok.Builder;

@Builder
public record UpdateResponse(int rows) {
}
