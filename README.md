<p align="center">
  <a href="https://db2rest.com" target="blank"><img src="https://db2rest.com/img/logo-db2rest.svg" width="120" alt="DB2Rest Logo" /></a>
</p>


<h3 align="center">No-Code REST API for Databases in Minutes. Deliver applications 20x faster, open up legacy data securely for innovation and integration.</h3>
  

# DB2Rest

> Don't write any database access code, Install DB2Rest instead. 

DB2Rest is an [Apache 2.0 Licensed](https://github.com/kdhrubo/db2rest/blob/master/LICENSE) open-source low-code middleware that provides secure and blazing fast data access layer over
your existing or new databases. You can connect to the most widely used databases like PostgreSQL, MySQL, Oracle, SQL Server, and MongoDB to build a REST API in minutes without writing any code.
You can now focus on building business logic and beautiful user interfaces at speed.



![GitHub issues](https://img.shields.io/github/issues/kdhrubo/db2rest)
![GitHub commit activity (branch)](https://img.shields.io/github/commit-activity/w/kdhrubo/db2rest)
![GitHub top language](https://img.shields.io/github/languages/top/kdhrubo/db2rest)
![GitHub License](https://img.shields.io/github/license/kdhrubo/db2rest)


# Support

*Connect on Discord*

[![](https://dcbadge.vercel.app/api/server/gytFPNW656?theme=discord)](https://discord.gg/gytFPNW656)



# Contact

<help@db2rest.com>


# How does it work?

![DB2Rest- How it works?](assets/db2rest-hiw.png "DB2Rest")


The diagram above shows an application architecture with DB2Rest. DB2Rest provides secure access to the database as REST API within seconds of installation/deployment. 
The business logic can be written in your favorite technology frameworks for Java, PHP, Node, .NET, or using any serverless framework. The business logic layer uses the database access layer (DBAL) provided
by DB2Rest to query and modify data. The user experience layer can be developed using popular front-end frameworks or low-code/no-code platforms. This layer can make use of the business logic layer or directly access secure data layer provided by DB2Rest.

## Benefits

    - No code, no SQL knowledge required, instead use simple REST Query Language (RQL) to retrieve data.
    - Accelerate application development by 30x. 
    - Unlock databases - secure REST API access for legacy data.
    - Blazing fast - No ORM, Single SQL Statement, 1 Database round-trip, does not use code generation.
    - Support for advanced custom queries, bulk data insert, and remote stored procedure calls. 
    - Best practices for transaction management, connection pooling, encryption, security - RBAC / data entitlement.
    - Deploy and run anywhere - on-premise, VM, Kubernetes, or any cloud. 
    - Zero downtime - adjusts to your evolving database schema. 
    - Compatible with DevOps processes. 


# Installation 

## On Premise / On Virtual Machines (VM) 

Refer to [installation quickstart documentation](https://db2rest.com/docs/intro).


## With Docker

Refer to [installation quickstart documentation](https://db2rest.com/docs/installation-running-with-docker).



# Supported Databases

- **PostgreSQL**
- **MySQL**

# Supported Features

**Save Record (Create)**

- [x] Single record.
- [x] Bulk records.

**Query (Read)**

- [x] Row Filtering with rSQL DSL.
- [x] Column Selection
- [x] Rename Columns / Alias
- [x] Join - Inner
- [x] Include Join Columns
- [x] Pagination - Limit & Offset
- [X] Sort / Order by
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


# Request parameters for Pagination

| Parameter Name | Description                                               | 
|----------------|-----------------------------------------------------------|
| page           | Page you want to retrieve, 0 indexed and defaults to 0.   | 
| size           | Size of the page you want to retrieve, defaults to 20.    | 

# Request parameters for Sorting

| Parameter Name | Description                                               | 
|----------------|-----------------------------------------------------------|
| sort           | Properties that should be sorted by in the format property,property(,ASC|DESC). Default sort direction is ascending. Use multiple sort parameters if you want to switch directions, e.g. ?sort=firstname&sort=lastname,asc.  | 

# RSQL 

RSQL is a query language for parametrized filtering of entries in RESTful APIs. It’s based on [FIQL](https://datatracker.ietf.org/doc/html/draft-nottingham-atompub-fiql-00 "Feed Item Query Language") (Feed Item Query Language) – a URI-friendly syntax for expressing filters across the entries in an Atom Feed. FIQL is great for use in URI; there are no unsafe characters, so URL encoding is not required. On the other side, FIQL’s syntax is not very intuitive and URL encoding is not always a big deal, so RSQL also provides a friendlier syntax for logical operators and some of the comparison operators.

For example, you can query your resource like this: `/movies?query=name=="Kill Bill";year=gt=2003` or `/movies?query=director.lastName==Nolan and year>=2000`. See examples below.

RSQL expressions in both FIQL-like and alternative notation: 

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
   
Use the colon separator `:` to map a column to an alias.  This will retrieve all the rows and columns from the database. Avoid if the table has large number of rows, use pagination instead.
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


**8. Insert Records**

Refer to [insert documentation](https://db2rest.com/docs/category/insert-records).



**10. Update record with filter**

This PATCH operation updates the film with id = 1001 which was inserted earlier. Filter is optional. In this case it will update all the rows in the table. Hence, use PATCH update with care.

```Shell
curl --request PATCH \
  --url 'http://localhost:8080/film?filter=film_id%3D%3D1001' \
  --header 'Content-Profile: sakila' \
  --header 'Content-Type: application/json' \
  --header 'User-Agent: insomnia/8.4.5' \
  --data '{
	
	"rental_rate" : 1.99,
	"length" : 92
	
}'
```
**HTTPie**

```Shell
echo '{
	
	"rental_rate" : 1.99,
	"length" : 92
	
}' |  \
  http PATCH 'http://localhost:8080/film?filter=film_id%3D%3D1001' \
  Content-Profile:sakila \
  Content-Type:application/json \
  User-Agent:insomnia/8.4.5
```


**11. Delete Records**

Refer to [delete documentation](https://db2rest.com/docs/category/delete-records).




**13. Offset pagination**

This GET operation will fetch results of the query in chunks of pages - 2 records at a time.

```Shell
curl --request GET \
  --url 'http://localhost:8080/actor?select=actor_id,first_name,last_name&filter=first_name=="PENELOPE"&page=0&size=2' \
  --header 'Accept-Profile: sakila' \
  --header 'User-Agent: insomnia/8.4.5'
```
**HTTPie**

```Shell
http GET 'http://localhost:8080/actor?select=actor_id,first_name,last_name&filter=first_name=="PENELOPE"&page=0&size=2' \
  Accept-Profile:sakila \
  User-Agent:insomnia/8.4.5
```


**14. Sorting with Offset pagination**

This GET operation will fetch results of the query in chunks of pages - 2 records at a time and sorted.

```Shell
curl --request GET \
  --url 'http://localhost:8080/actor?select=actor_id,first_name,last_name&filter=first_name=="PENELOPE"&page=0&size=2&sort=actor_id&sort=first_name,DESC' \
  --header 'Accept-Profile: sakila' \
  --header 'User-Agent: insomnia/8.4.5'
```
**HTTPie**

```Shell
http GET 'http://localhost:8080/actor?select=actor_id,first_name,last_name&filter=first_name=="PENELOPE"&page=0&size=2&sort=actor_id&sort=first_name,DESC' \
  Accept-Profile:sakila \
  User-Agent:insomnia/8.4.5
```

# HTTP Headers

(soon to be OPTIONAL see Issue [#80](https://github.com/kdhrubo/db2rest/issues/80) ) In case multiple schemas have been configured for use (with - DB_SCHEMAS parameter), it is mandatory to specify the schema to use with the HTTP HEADER - *Accept-Profile*. If no header is specified, the request will be rejected as a security measure. DB2Rest will not allow querying tables outside the schemas set.

# Roadmap

- [ ] Support for Oracle - Coming March 2024
- [ ] Support for SQL Server - Coming April 2024
- [ ] Support for MongoDB
- [ ] Change data capture (CDC) with Webhooks to notify of database changes
- [ ] JSON data type support
- [x] Stored procedure, stored function calls - *DONE*
- [ ] Support for tenant id column for multi-tenancy 
- [x] Twitter Handle - DONE [DB2Rest](https://twitter.com/DB2Rest)
- [x] New Documentation Website with Docusaurus - [Website](https://db2rest.com)
- [x] Custom SQL query Execution via HTTP POST **DONE**
- [x] Count query support - *DONE*
- [ ] Exists query support
- [ ] Data access control
- [ ] Data Privacy, Encryption, Masking for read and update.
- [x] TSID support 
- [ ] Data transformation (some features possibly ENTERPRISE EDITION)
- [x] Multi-table implicit Join - with one to many based on select path.
- [ ] Outer Join
- [ ] Cross Join
- [ ] Support - DB Sharding (ENTERPRISE EDITION)
- [ ] Support - Rate Limiting
- [ ] Audit columns handling - created date, last updated date, created by, last updated by
- [ ] Audit table / data diff handling
- [ ] Version column handling
- [x] Offset & limit pagination - *DONE* 
- [ ] SEEK method for pagination
- [ ] Caching - Redis support (Requested on Redit)
- [ ] Open API specification 3.x - Coming Feb 2024 
- [ ] Aggregate function
- [ ] mTLS/Certificate auth 
- [ ] JWT/JWKS support (ENTERPRISE EDITION)
- [ ] API Key support - unkey.dev or custom - Coming Feb 2024
- [ ] Open policy agent support (ENTERPRISE EDITION)
- [ ] OSO support (ENTERPRISE EDITION)
- [ ] Integration with AWS KMS, Hashicorp Vault
- [ ] Observability - Data dog (requested by a financial company)
- [ ] HTTP2 support - (requested by a financial company)
- [ ] Support for Grafana & Prometheus - (requested by a financial company)
- [ ] Configuration Docs - AWS RDS Postgres, Aurora Postgres, DB User role
- [ ] Youtube videos
- [ ] Native binary image - simplify installation
- [ ] Support for scripting (extensibility) - Python, Apache Groovy, Javascript/VS8 
- [ ] Deploy to Digital Ocean
- [ ] Deploy to GCP
- [ ] Ability to insert into multiple tables
- [ ] Batch loading of remote files

