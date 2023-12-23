# DB2Rest
Instant REST API over your existing or new database in minutes. 

## Benefits
    - Coming soon

## Supported Databases

### Postgresql (In progress)

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

### MySQL (Planned)

### SQL Server 

### Oracle 


### Mongodb





