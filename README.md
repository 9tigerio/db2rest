# DB2Rest
Instant REST API over your existing or new database in minutes. 

## Benefits
    - Coming soon

## Supported Databases

### Postgresql (In progress)

[ ] Save Record (Create) - In progress

    - [X] Single record
    - [X] Bulk records - JSON 
        - [] Support batch insert 

[*] Query (Read)  - In progress

    - [X] Row Filtering
        Supported with rSQL.
    - [X] Column Selection
    - [X] Rename Columns / Alias
    - [X] Join
    - [ ] Pagination
    - [ ] Sorting
    - [ ] Group By
    - [ ] Count
    - [ ] Join column filter


[ ] Edit (Update) 

    - [X] Patch
    - [X] Patch with row filtering
     
    
[ ] Purge (Delete) 

    - [X] Delete with row filter.
    - [X] Safe delete.

[*] Transactions (In progress)

[ ] Multi-tenancy

[ ] Schema

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





