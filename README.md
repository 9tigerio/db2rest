# DB2Rest
Instant REST API over your existing or new database in minutes. No code data access layer to build new applications at speed or unlock value of existing data. 

## Benefits
    - Coming soon

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

### Postgresql (In progress)

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

**[X] Schema Support - [Completed]**

**[ ] JSON Column / Data support**


**[ ] TSID Support**

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

### MySQL (In Progress)

**[X] Save Record (Create) -  [Completed]**

    - [X] Single record
    - [X] Bulk records - JSON 


**[*] Query (Read)  - In progress**

    - [X] Row Filtering with rSQL DSL.
    - [X] Column Selection
    - [X] Rename Columns / Alias
    - [X] Join
    - [ ] Pagination
    - [ ] Sorting
    - [ ] Group By
    - [ ] Count
    - [ ] Join column filter


**[X] Edit - [Completed]**

    - [X] Patch
    - [X] Patch with row filtering


**[X] Purge (Delete) - [Completed]**

    - [X] Delete with row filter.
    - [X] Safe delete.

**[X] Transactions - [Completed]**

**[*] Multi-tenancy (In progress)**

    - [ ] tenant_id column
    - [X] Schema per tenant
    - [ ] Database per tenant

**[X] Schema Support - [Completed]**

**[ ] JSON Column / Data support**


**[ ] TSID Support**

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

### SQL Server 

### Oracle 


### Mongodb





