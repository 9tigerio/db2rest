package com.homihq.db2rest.core.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class DataSource {
     String type;
     String includeSchemas;
     String includeTables;
     String includeCatalogs;

     public String getSchemaPattern(){
          return StringUtils.equals(includeSchemas,"") ? null : includeSchemas;
     }

     public String getTablePattern(){
          return StringUtils.equals(includeTables,"") ? null : includeTables;
     }

     public String getCatalogPattern(){
          return StringUtils.equals(includeCatalogs,"") ? null : includeCatalogs;
     }
}
