package com.homihq.db2rest.jdbc.rest.update.dto;

import lombok.Builder;

@Builder
public record UpdateResponse(int rows) {
}
