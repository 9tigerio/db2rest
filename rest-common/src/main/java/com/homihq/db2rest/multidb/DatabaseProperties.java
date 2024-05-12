package com.homihq.db2rest.multidb;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
@ConfigurationProperties(prefix = "app")
public class DatabaseProperties {

    @Setter
    //private List<Map<String,String>> databases;

    private List<DatabaseConnectionDetail> databases;
}
