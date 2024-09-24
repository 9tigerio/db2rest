package com.homihq.db2rest;

import com.homihq.db2rest.jdbc.multidb.RoutingDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.adapter.jdbc.JdbcSchema;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

@Component
@Slf4j
@RequiredArgsConstructor
public class Federation implements CommandLineRunner {

    private final DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {

        log.info("Federation started");

        // Create Calcite connection
        Properties info = new Properties();
        info.setProperty("lex", "JAVA");
        Connection connection = DriverManager.getConnection("jdbc:calcite:", info);
        CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);

        // Get root schema
        SchemaPlus rootSchema = calciteConnection.getRootSchema();

        if(dataSource instanceof RoutingDataSource) {
            Map<Object, DataSource> dataSourceMap = ((RoutingDataSource) dataSource).getResolvedDataSources();

            for(Object dbId : dataSourceMap.keySet()) {
                log.info("** dbId - {}", dbId);
                if(StringUtils.equalsAnyIgnoreCase(dbId.toString(), "DB1")) {
                    DataSource ds = dataSourceMap.get(dbId);
                    JdbcSchema jdbcSchema =
                            JdbcSchema.create(rootSchema, "postgres", ds, null, "public");
                    rootSchema.add("postgres", jdbcSchema);
                }
                else if(StringUtils.equalsAnyIgnoreCase(dbId.toString(), "DB2")) {

                    DataSource ds = dataSourceMap.get(dbId);
                    JdbcSchema jdbcSchema =
                            JdbcSchema.create(rootSchema, "mysql", ds, null, "ecomm");
                    rootSchema.add("mysql", jdbcSchema);
                }

                log.info("Added child schema");
            }

        }
        // Execute the join query
        String sql = """
    SELECT o.order_id,c.customer_id, c.first_name, c.last_name 
                     FROM postgres.`order` o 
                     JOIN mysql.customer c ON o.customer_id = c.customer_id
""";


        Statement statement = calciteConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        // Process and print results
        while (resultSet.next()) {
            System.out.println("Order ID: " + resultSet.getInt("order_id") +
                    ", Customer Id: " + resultSet.getInt("customer_id") +
                    ", First Name: " + resultSet.getString("first_name") +
                    ", Last Name: " + resultSet.getString("last_name"));
        }

        // Close resources
        resultSet.close();
        statement.close();
        connection.close();

    }
}
