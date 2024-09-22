package com.homihq.db2rest.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
  info = @Info(
  title = "DB2REST",
  description = "Low-code REST Data API for your databases" ,
  contact = @Contact(
    name = "DB2REST Support",
    url = "https://db2rest.com",
    email = "help@db2rest.com"
  ),
  license = @License(
    name = "Apache-2.0 Licence",
    url = "https://github.com/kdhrubo/db2rest?tab=Apache-2.0-1-ov-file"))
)
class OpenAPIConfiguration {
}
