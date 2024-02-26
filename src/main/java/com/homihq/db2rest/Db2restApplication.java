package com.homihq.db2rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Db2restApplication {

    public static void main(String[] args) {
        SpringApplication.run(Db2restApplication.class, args);
    }

}
