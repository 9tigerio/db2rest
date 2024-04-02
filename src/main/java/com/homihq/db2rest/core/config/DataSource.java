package com.homihq.db2rest.core.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Data
@Slf4j
public class DataSource {
     String type;
     String includeSchemas;
     String includeTables;
     String includeCatalogs;

     public String [] getSchemas(){

          log.info("Included schemas - {}", includeSchemas);

          return
          StringUtils.isNotBlank(includeSchemas) ? includeSchemas.split(",") : null;


     }

     public String getTablePattern(){
          return StringUtils.equals(includeTables,"") ? null : includeTables;
     }

     public String getCatalogPattern(){
          return StringUtils.equals(includeCatalogs,"") ? null : includeCatalogs;
     }
}
