#!/bin/bash

# Wait for services to be fully up
sleep 15

# Base URL for DB2Rest API
BASE_URL="http://localhost/emp/employee"

# Insert an employee
echo "Inserting employee..."
curl -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Smith"}'

# Query all employees
echo -e "\nQuerying all employees..."
curl -X GET $BASE_URL
