package com.homihq.db2rest.config;

import lombok.Data;

@Data
public class MultiTenancy {
     private boolean enabled;
     private TenancyMode mode;
}
