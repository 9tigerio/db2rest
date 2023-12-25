# DB2Rest

DB2Rest is an [Apache 2.0 Licensed](https://github.com/kdhrubo/db2rest/blob/master/LICENSE) open-source low-code middleware that provides secure and blazing fast data access layer over
your existing or new databases. You can connect to most widely used databases like PostgreSQL, MySQL, Oracle, SQL Server, MongoDB to build REST API in minutes without any coding.
You can now focus on building business logic and beautiful user interfaces at speed. 
 

# How it works?

![DB2Rest- How it works?](assets/db2rest-hiw.png "DB2Rest")


## Benefits
    - Accelerate applicaton development
    - Unlock databases



# Installation 

    - Without Docker 
    - With Docker

## Configuration Parameters


| Sl#   | Parameter Name        | Description                                             | Allowed Values/Examples                                                                                                                                   |
|-------|-----------------------|---------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1.    | ALLOW_UNSAFE_DELETE   | Allow deletion of table rows without a filter criteria. | <ul><li>true</li><li>false (default)</li></ul>                                                                                                            |
| 2.    | DB_URL                | Database connection URL in JDBC format.                 | <ul><li>jdbc:mysql://<DB_SERVER_HOST>:<DB_PORT>/<DB_NAME> (MySQL)</li><li>jdbc:postgresql://<DB_SERVER_HOST>:<DB_PORT>/<DB_NAME> (PostgreSQL)</li></ul>   |
| 3.    | DB_USER               | Database user name                                      | -                                                                                                                                                         |
| 4.    | DB_PASSWORD           | Database password                                       | -                                                                                                                                                         |
| 5.    | DB_SCHEMAS            | A comma separated list of allowed schemas               | e.g - SAKILA,WORLD                                                                                                                                        |
| 6.    | MULTI_TENANCY_ENABLED | Run in multi tenancy mode. DB_SCHEMAS will be ignored if multi-tenancy is ON| <ul><li>true</li><li>false (default)</li></ul>                                                                                        |
| 7.    | MULTI_TENANCY_MODE    | Multi-tenancy mode                                      | <ul><li>TENANT_ID (not supported) </li><li>SCHEMA (supported)</li><li>DB (not supported)</li></ul>      |


## Supported Databases

- **PostgreSQL**
- **MySQL**

**Save Record (Create)**

    - [x] Single record.
    - [x] Bulk records.


**Query (Read)**

    - [x] Row Filtering with rSQL DSL.
    - [x] Column Selection
    - [x] Rename Columns / Alias
    - [x] Join - Inner
    - [x] Include Join Columns
    - [ ] Pagination
    - [ ] Sorting
    - [ ] Group By
    - [ ] Count
    - [ ] Join column filter


**Edit**

    - [x] Patch
    - [x] Patch with row filtering


**Purge (Delete)**

    - [x] Delete with row filter.
    - [x] Safe delete.

**Transactions**

    - [x] Readonly for Select
    - [x] Supported for Save, Edit, Purge 

**Multi-tenancy**

    - [ ] Tenant Id column
    - [x] Schema per tenant
    - [ ] Database per tenant

**Schema Support**

    - [x] Multiple schema support


#### Supported Operators

| Operator    | Postgresql    | Description             | Supported |
|-------------|---------------|-------------------------|-----------|
| ==          | =             | equals                  | [X]       |
| >           | >             | greater than            | [X]       |
| =gt=        | >             | greater than            | [X]       |
| >=          | >=            | greater than or equal   | [X]       |
| =gt=        | >             | greater than or equal   | [X]       |
| <           | <             | less than               | [X]       |
| =lt=        | <             | less than               | [X]       |
| <=          | <=            | less than or equal      | [X]       |
| =le=        | <=            | less than or equal      | [X]       |
| =in=        | <=            | in                      | [X]       |
| =out=       | <=            | not in                  | [X]       |
| =like=      | like          | like                    | [X]       |
| =startWith= | like          | start with ex - Joy%    | [X]       |
| =endWith=   | like          | start with ex - %Ful    | [X]       |


### Examples ###

- GET All (https://<db2rest-url>/actor) : This will retrieve all the rows and columns from the database. Avoid if the table has large number of rows, use pagination instead.
- GET All with column filter: (https://<db2rest-url>/actor?select=actor_id,first_name,last_name) This will retrieve all the rows and ~only the speficied columns~ from the database. Avoid if the table has large number of rows, use pagination instead.
- GET All with row filter: 


### Headers ###

In case multiple schemas have been configured for use (with - DB_SCHEMAS parameter), it is mandatory to specificy the schema to use with the HTTP HEADER - ~Accept-Profile~. If no header is specified, the request will be rejected as a security measure. DB2Rest will not allow querying tables outside the schemas set 


# Roadmap

1. Support for Oracle.
2. Support for SQL Server.
3. Support for MongoDB.
4. Change data capture (CDC) with Webhooks to notify of database changes.
5. JSON data type support.
6. Stored procedure, stored function calls. 
7. Support for tenant id column for multi-tenancy. 
8. Twitter Handle.
9. New expanded Documentation Website with Docusaurus,
10. Support custom query. 
11. Data access control. 
12. Data Privacy.
13. TSID Support.
14. Data transformation.

