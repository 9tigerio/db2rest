# DB2Rest - IBM DB2 Dialect

This module provides IBM DB2 database support for the DB2Rest platform, enabling instant REST API generation for DB2 databases.

## Overview

The DB2 dialect module extends DB2Rest to support IBM DB2 databases, providing:
- Automatic REST API generation for DB2 tables and views
- Support for DB2-specific data types (JSON, XML, TIMESTAMP)
- Proper identifier quoting using double quotes
- Integration with DB2's SQL syntax and features

## Features

- ✅ **Full DB2 Support**: Compatible with IBM DB2 11.5.8.0 and later
- ✅ **Data Type Support**: JSON, XML, TIMESTAMP, and all standard SQL types
- ✅ **Identifier Quoting**: Proper handling of DB2 identifiers with double quotes
- ✅ **Table Operations**: Full CRUD operations on DB2 tables
- ✅ **Schema Support**: Multi-schema database support
- ✅ **Connection Pooling**: Efficient database connection management

## Supported DB2 Versions

- IBM DB2 11.5.8.0+
- IBM DB2 for Linux, UNIX, and Windows (LUW)
- IBM DB2 on Cloud

## Dependencies

The DB2 dialect uses the official IBM DB2 JDBC driver:

```xml
<dependency>
    <groupId>com.ibm.db2</groupId>
    <artifactId>jcc</artifactId>
    <version>11.5.8.0</version>
</dependency>
```

## Configuration

### Database Connection

Configure your DB2 connection in the application properties:

```properties
# DB2 Database Configuration
spring.datasource.url=jdbc:db2://localhost:50000/empdb
spring.datasource.username=db2inst1
spring.datasource.password=db2inst1
spring.datasource.driver-class-name=com.ibm.db2.jcc.DB2Driver

# Optional: Schema filtering
db2rest.included-schemas=DB2INST1,EMP
```

### Docker Compose Setup

A complete Docker Compose configuration is provided for testing:

```yaml
services:
  db2rest-service:
    build:
      context: ../../
      dockerfile: Dockerfile
    ports:
      - 80:8080
    environment:
      DB_URL: "jdbc:db2://employee-db2:50000/empdb"
      DB_USER: "db2inst1"
      DB_PASSWORD: "db2inst1"
      INCLUDED_SCHEMAS: "DB2INST1,EMP"
    depends_on:
      - employee-db2

  employee-db2:
    image: "ibmcom/db2:11.5.8.0"
    container_name: "employee-db2"
    ports:
      - 50000:50000
    environment:
      - LICENSE=accept
      - DB2INSTANCE=db2inst1
      - DB2INST1_PASSWORD=db2inst1
      - DBNAME=empdb
    volumes:
      - db2-data:/database

volumes:
  db2-data:
```

## Quick Start

1. **Start DB2 Database**:
   ```bash
   cd docker-compose/db2
   docker-compose up -d employee-db2
   ```

2. **Start DB2Rest**:
   ```bash
   docker-compose up -d db2rest-service
   ```

3. **Access REST API**:
   - API Base URL: `http://localhost/v1`
   - Health Check: `http://localhost/actuator/health`
   - Database Info: `http://localhost/v1/_meta/db`

## API Examples

### Get All Tables
```bash
curl http://localhost/v1/_meta/tables
```

### Query a Table
```bash
curl http://localhost/v1/EMPLOYEE
```

### Insert Data
```bash
curl -X POST http://localhost/v1/EMPLOYEE \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "department": "Engineering"}'
```

### Update Data
```bash
curl -X PUT http://localhost/v1/EMPLOYEE/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "Jane Doe", "department": "Marketing"}'
```

## Testing

### Unit Tests
Run the unit tests to verify dialect functionality:

```bash
mvn test -pl db2rest-dialects/db2-dialect -am
```

### Unit Tests
The DB2 dialect includes comprehensive unit tests that verify:
- Database type detection
- SQL identifier quoting
- Table name rendering
- Data type processing
- Dialect configuration

## Development

### Building the Module

```bash
# Build the entire project
mvn clean package

# Build only the DB2 dialect
mvn clean package -pl db2rest-dialects/db2-dialect -am
```

### Running Tests

```bash
# Run all tests
mvn test

# Run only DB2 dialect tests
mvn test -pl db2rest-dialects/db2-dialect -am

# Run with specific revision
mvn test -Drevision=1.6.5
```

## Troubleshooting

### Common Issues

1. **Connection Refused**: Ensure DB2 is running and accessible on the specified port
2. **Authentication Failed**: Verify username/password and DB2 instance configuration
3. **Schema Not Found**: Check that the specified schemas exist and are accessible
4. **Driver Not Found**: Ensure IBM DB2 JDBC driver is in the classpath

### Logging

Enable debug logging for DB2 operations:

```properties
logging.level.com.db2rest.jdbc.dialect.DB2RestDB2Dialect=DEBUG
logging.level.com.ibm.db2.jcc=DEBUG
```

## Contributing

See the main [CONTRIBUTING.md](../../CONTRIBUTING.md) for general contribution guidelines.

### DB2-Specific Contributions

When contributing to the DB2 dialect:

1. Ensure all tests pass: `mvn test -pl db2rest-dialects/db2-dialect -am`
2. Add tests for new features
3. Update documentation for any API changes
4. Follow the commit convention: `feat(db2-dialect): description`

## License

This module is part of DB2Rest and is licensed under the [Apache-2.0 License](../../LICENSE).
