package com.homihq.db2rest.d1.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record D1Record(String type, String name, @JsonProperty("tbl_name") String tblName) {
}
