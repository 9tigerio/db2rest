package com.homihq.db2rest.d1.model;


public record D1Column(int cid , String name, String type, boolean notNull, Object defaultValue, boolean pk) {}
