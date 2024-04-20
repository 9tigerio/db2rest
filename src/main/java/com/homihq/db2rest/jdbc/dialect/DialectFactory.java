package com.homihq.db2rest.jdbc.dialect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanCreationException;

public class DialectFactory {

    public static Dialect getDialect(String productName, int majorVersion) {
        ObjectMapper objectMapper = new ObjectMapper();
        if(isMySQL(productName,majorVersion)) {
            return new MySQLDialect(objectMapper);
        }
        else if(isPostGreSQL(productName,majorVersion)) {
            return new PostGreSQLDialect(objectMapper);
        }
        else if(isOracle(productName,majorVersion)) {
            return new OracleDialect(objectMapper, productName, majorVersion);
        }
        else if(isMariaDB(productName,majorVersion)) {
            return new MariaDBDialect(objectMapper);
        }
        else {
            throw new BeanCreationException("Unable to create database dialect.");
        }
    }

    private static boolean isOracle(String productName, int majorVersion) {
        return StringUtils.containsIgnoreCase(productName, "Oracle");
    }
    private static boolean isMariaDB(String productName, int majorVersion) {
        return StringUtils.containsIgnoreCase(productName, "MariaDB");
    }

    private static boolean isMySQL(String productName, int majorVersion) {
        return StringUtils.containsIgnoreCase(productName, "MySQL");
    }

    private static boolean isPostGreSQL(String productName, int majorVersion) {
        return StringUtils.containsIgnoreCase(productName, "PostGreSQL");
    }
}
