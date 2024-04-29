package com.homihq.db2rest.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class MultiTenancy {
     private boolean enabled;
     private TenancyMode mode;

     public boolean isSchemaBased() {
          return enabled && StringUtils.equalsIgnoreCase(mode.name(), TenancyMode.SCHEMA.name());
     }
}
