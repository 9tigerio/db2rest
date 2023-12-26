package com.homihq.db2rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@EnableSpringDataWebSupport

@SpringBootApplication
public class Db2restApplication {

    public static void main(String[] args) {
        SpringApplication.run(Db2restApplication.class, args);
    }

}
