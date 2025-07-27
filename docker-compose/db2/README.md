# DB2Rest with IBM DB2 Database

This directory contains Docker Compose configuration for running DB2Rest with IBM DB2 database.

## Prerequisites

- Docker and Docker Compose installed
- At least 4GB of available RAM (DB2 container requires significant memory)
- Accept IBM DB2 license terms

## Quick Start

1. Start the services:
   ```bash
   docker-compose up -d
   ```

2. Wait for DB2 to initialize (this can take several minutes on first run):
   ```bash
   docker-compose logs -f employee-db2
   ```

3. Once DB2 is ready, access DB2Rest API at: http://localhost

## Configuration

### Environment Variables

- `DB_URL`: JDBC connection URL for DB2
- `DB_USER`: Database username (default: db2inst1)
- `DB_PASSWORD`: Database password (default: db2inst1)
- `INCLUDED_SCHEMAS`: Comma-separated list of schemas to include

### DB2 Container Configuration

- **Image**: `ibmcom/db2:11.5.8.0`
- **Port**: 50000 (mapped to host port 50000)
- **Database**: empdb
- **Instance**: db2inst1
- **License**: Automatically accepted

## Sample Data

The initialization script creates:

- `EMP` schema with sample employee and department data
- `EMPLOYEE` table with 5 sample records
- `DEPARTMENT` table with 4 sample departments
- Proper foreign key relationships and indexes

## API Examples

Once the services are running, you can test the API:

```bash
# Get all employees
curl http://localhost/emp/employee

# Get employee by ID
curl http://localhost/emp/employee/1

# Get all departments
curl http://localhost/emp/department

# Create new employee
curl -X POST http://localhost/emp/employee \
  -H "Content-Type: application/json" \
  -d '{
    "first_name": "New",
    "last_name": "Employee",
    "email": "new.employee@company.com",
    "salary": 60000,
    "department_id": 1
  }'
```

## Troubleshooting

### DB2 Container Issues

1. **Container fails to start**: Ensure you have enough memory allocated to Docker
2. **License issues**: The container automatically accepts the license, but ensure your usage complies with IBM terms
3. **Slow startup**: DB2 initialization can take 5-10 minutes on first run

### Connection Issues

1. **Check DB2 status**:
   ```bash
   docker-compose exec employee-db2 su - db2inst1 -c "db2 list active databases"
   ```

2. **Test connection**:
   ```bash
   docker-compose exec employee-db2 su - db2inst1 -c "db2 connect to empdb"
   ```

## Cleanup

To stop and remove all containers and volumes:

```bash
docker-compose down -v
```

## Notes

- DB2 requires accepting IBM license terms
- The container uses significant system resources
- Data is persisted in Docker volumes
- First startup takes longer due to DB2 initialization
