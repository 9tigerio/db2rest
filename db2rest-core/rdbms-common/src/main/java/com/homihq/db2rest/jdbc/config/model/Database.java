package com.homihq.db2rest.jdbc.config.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Database {

    ORACLE("Oracle"),
    MSSQL("Microsoft SQL Server"),
    MYSQL("MySQL"),
    POSTGRESQL("PostgreSQL"),
    MARIADB("MariaDB"),
    SQLITE("SQLite");

    private final String productName;

}
