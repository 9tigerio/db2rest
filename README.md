# DB2Rest

DB2Rest is an [Apache 2.0 Licensed](https://github.com/kdhrubo/db2rest/blob/master/LICENSE) open-source low-code middleware that provides secure and blazing fast data access layer over
your existing or new databases. You can connect to most widely used databases like PostgreSQL, MySQL, Oracle, SQL Server, MongoDB to build REST API in minutes without writing any code.
You can now focus on building business logic and beautiful user interfaces at speed. 
 

# How it works?

![DB2Rest- How it works?](assets/db2rest-hiw.png "DB2Rest")


The diagram above shows an application architecture with DB2Rest. DB2Rest provides secure access to the database as REST API within seconds of installation/deployment. 
The business logic can be written in your favorite technology frameworks for Java, PHP, Node, .NET or using any serverless framework. The business logic layer uses the database access layer (DBAL) provided
by DB2Rest to query and modify data. The user experience layer can be developed using popular front-end frameworks or low code/node code platforms. This layer can make use of the business logic layer or directly access secure data layer provided by DB2Rest.

## Benefits
    - Accelerate applicaton development
    - Unlock databases
    - Blazing fast - No ORM, Single SQL Statement, 1 Database round-trip

TODO - Add more details


# Installation 

## On Premise / On Virtual Machines (VM) 

DB2Rest needs Java Runtime 21+ to run. This is because it is compiled with Java 21 and makes uses of Java Virtual Thread feature
for high scalability. In other words, DB2Rest is capable of handling very high volume of requests even on a single node. 

In case you are deploying DB2Rest on bare metal box or a VM on any cloud like Amazon EC2 or DigitalOcean Droplet, follow the steps below:

### 1. Install JDK 21+

Download JDK 21 or above. There are many flavors of JDK available from different vendors like Oracle, AWS, OpenJDK. 
Open JDK can be downloaded from here - https://jdk.java.net/21/. This article from https://www.theserverside.com/blog/Coffee-Talk-Java-News-Stories-and-Opinions/How-to-install-Java-21 provides a step by step guide 
to install OpenJDK 21. 


### 2. Download DB2Rest

Now that you have successfully downloaded, installed and verified Java 21, the next step is to get DB2Rest. DB2Rest is shipped
as a single executable Java Archive or jar file So its super easy to get up and running under a minute. 

In order to download the latest edition of DB2Rest click [here](https://pub-d494e6f63184463298f75f4d77bde7cb.r2.dev/db2rest-0.0.4.jar "here").

### 3. Run DB2Rest.

DB2Rest is just 60Mb and is immediately runnable. Fire up a terminal and execute the command below:

```Shell
$ java -DALLOW_UNSAFE_DELETE=false -DDB_PASSWORD=[DATABASE_PASSWORD] -DDB_SCHEMAS=[COMMA_SEPARATED_LIST_OF_DB_SCHEMAS] -DDB_URL=jdbc:mysql://localhost:3306/sakila -DDB_USER=[DATABASE_USER] -DMULTI_TENANCY_ENABLED=false -DMULTI_TENANCY_MODE=SCHEMA -Dspring.profiles.active=local -jar db2rest-0.0.4.jar
``` 

Replace the values for the following:
- DATABASE_PASSWORD
- COMMA_SEPARATED_LIST_OF_DB_SCHEMAS
- DATABASE_USER

The above example is for connecting to MySQL database. 

Once this command is executed, within a few seconds, DB2Rest is ready to service your data access requests.  

## With Docker

Coming soon. 

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


# Supported Databases

- **PostgreSQL**
- **MySQL**

# Supported Operations

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


# Supported Operators

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


# Examples 

**1. Get all Actors**
   
This will retrieve all the rows and columns from the database. Avoid if the table has large number of rows, use pagination instead.

**cURL**

```Shell
curl --request GET \
  --url http://localhost:8080/actor \
  --header 'Accept-Profile: sakila' \
  --header 'User-Agent: insomnia/8.4.5'
```
**HTTPie**

```Shell
http GET http://localhost:8080/actor \
  Accept-Profile:sakila \
  User-Agent:insomnia/8.4.5
```



**2. Get all Actors with Column Filter**
   
This will retrieve all the rows but *only the speficied columns* from the database. Avoid if the table has large number of rows, use pagination instead.

**cURL**

```Shell
curl --request GET \
  --url 'http://localhost:8080/actor?select=actor_id,first_name,last_name' \
  --header 'Accept-Profile: sakila' \
  --header 'User-Agent: insomnia/8.4.5'
```
**HTTPie**

```Shell
http GET 'http://localhost:8080/actor?select=actor_id,first_name,last_name' \
  Accept-Profile:sakila \
  User-Agent:insomnia/8.4.5
```



**3. Get all Actors with Row Filter**

This will retrieve all the rows with specified columns or all columns from the database. However this will also filter out rows based on the filtering criterias specified in the *filter* request
parameter. The filter uses RSQL - REST SQL format. The query below retrieves 2 columns 'first_name', 'last_name' from 'actor' table if the 'first_name' is 'PENELOPE'. 

**cURL**

```Shell
curl --request GET \
  --url 'http://localhost:8080/actor?select=actor_id,first_name,last_name&filter=first_name==PENELOPE' \
  --header 'Accept-Profile: sakila' \
  --header 'User-Agent: insomnia/8.4.5'
```
**HTTPie**

```Shell
http GET 'http://localhost:8080/actor?select=actor_id,first_name,last_name&filter=first_name==PENELOPE' \
  Accept-Profile:sakila \
  User-Agent:insomnia/8.4.5
```



**4. Get all Actors with Column Alias**
   
This will retrieve all the rows and columns from the database. Avoid if the table has large number of rows, use pagination instead.
This query shows how to change column name in the response JSON using Alias. 

**cURL**

```Shell
curl --request GET \
  --url 'http://localhost:8080/actor?select=actor_id:id,first_name:firstName,last_name:lastName' \
  --header 'Accept-Profile: sakila' \
  --header 'User-Agent: insomnia/8.4.5'
```
**HTTPie**

```Shell
http GET 'http://localhost:8080/actor?select=actor_id:id,first_name:firstName,last_name:lastName' \
  Accept-Profile:sakila \
  User-Agent:insomnia/8.4.5
```

**5. Get all Films Released in the year 2006 along with Language**

This will retrieve all the rows for the films released in 2006 along with its language for audio. 
DB2Rest is smart and detects the inner join relation between 'film' and 'language'.

**cURL**

```Shell
curl --request GET \
  --url 'http://localhost:8080/film?select=film_id:id,title,description,release_year:yearOfRelease&filter=release_year==2006&join=language[]' \
  --header 'Accept-Profile: sakila' \
  --header 'User-Agent: insomnia/8.4.5'
```
**HTTPie**

```Shell
http GET 'http://localhost:8080/film?select=film_id:id,title,description,release_year:yearOfRelease&filter=release_year==2006&join=language[]' \
  Accept-Profile:sakila \
  User-Agent:insomnia/8.4.5
```

*Note the square brackets are mandatory for join table.*


**6. Get all Films Released in the year 2006 along by Film Language with Join table fields**

The GET query above does not fetch the join table fields. The join table fields can be retrieved in the same way as the root table fields as shown below. 

```Shell
curl --request GET \
  --url 'http://localhost:8080/film?select=film_id:id,title,description,release_year:yearOfRelease&filter=release_year==2006&join=language[select=language_id:langId,name]' \
  --header 'Accept-Profile: sakila' \
  --header 'User-Agent: insomnia/8.4.5'
```
**HTTPie**

```Shell
http GET 'http://localhost:8080/film?select=film_id:id,title,description,release_year:yearOfRelease&filter=release_year==2006&join=language[select=language_id:langId,name]' \
  Accept-Profile:sakila \
  User-Agent:insomnia/8.4.5
```


**7. Get all Films Released in the year 2006 in English**

TODO


# HTTP Headers

In case multiple schemas have been configured for use (with - DB_SCHEMAS parameter), it is mandatory to specificy the schema to use with the HTTP HEADER - *Accept-Profile*. If no header is specified, the request will be rejected as a security measure. DB2Rest will not allow querying tables outside the schemas set 


# RSQL 

RSQL is a query language for parametrized filtering of entries in RESTful APIs. It’s based on [FIQL](https://datatracker.ietf.org/doc/html/draft-nottingham-atompub-fiql-00 "Feed Item Query Language") (Feed Item Query Language) – an URI-friendly syntax for expressing filters across the entries in an Atom Feed. FIQL is great for use in URI; there are no unsafe characters, so URL encoding is not required. On the other side, FIQL’s syntax is not very intuitive and URL encoding isn’t always that big deal, so RSQL also provides a friendlier syntax for logical operators and some of the comparison operators.

For example, you can query your resource like this: /movies?query=name=="Kill Bill";year=gt=2003 or /movies?query=director.lastName==Nolan and year>=2000. See examples below.


## Examples

Examples of RSQL expressions in both FIQL-like and alternative notation: 

```
- name=="Kill Bill";year=gt=2003
- name=="Kill Bill" and year>2003
- genres=in=(sci-fi,action);(director=='Christopher Nolan',actor==*Bale);year=ge=2000
- genres=in=(sci-fi,action) and (director=='Christopher Nolan' or actor==*Bale) and year>=2000
- director.lastName==Nolan;year=ge=2000;year=lt=2010
- director.lastName==Nolan and year>=2000 and year<2010
- genres=in=(sci-fi,action);genres=out=(romance,animated,horror),director==Que*Tarantino
- genres=in=(sci-fi,action) and genres=out=(romance,animated,horror) or director==Que*Tarantino

```

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
15. Automated Integration Test with Testcontainers.
16. Count query support
17. Exists query support
18. Multi-table Join - with one to many
19. Outer Join
20. Cross Join
21. Support - DB Sharding
22. Support - Rate Limiting. 

