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

TODO 

## With Docker

TODO

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

**GO**

```GO
package main

import (
	"fmt"
	"net/http"
	"io/ioutil"
)

func main() {

	url := "http://localhost:8080/actor"

	req, _ := http.NewRequest("GET", url, nil)

	req.Header.Add("User-Agent", "insomnia/8.4.5")
	req.Header.Add("Accept-Profile", "sakila")

	res, _ := http.DefaultClient.Do(req)

	defer res.Body.Close()
	body, _ := ioutil.ReadAll(res.Body)

	fmt.Println(res)
	fmt.Println(string(body))

}
```

**C#**

```csharp
var client = new RestClient("http://localhost:8080/actor");
var request = new RestRequest(Method.GET);
request.AddHeader("User-Agent", "insomnia/8.4.5");
request.AddHeader("Accept-Profile", "sakila");
IRestResponse response = client.Execute(request);
```

**Java**

```java
HttpResponse<String> response = Unirest.get("http://localhost:8080/actor")
  .header("User-Agent", "insomnia/8.4.5")
  .header("Accept-Profile", "sakila")
  .asString();
```
**Javascript**

```javascript
const data = null;

const xhr = new XMLHttpRequest();
xhr.withCredentials = true;

xhr.addEventListener("readystatechange", function () {
  if (this.readyState === this.DONE) {
    console.log(this.responseText);
  }
});

xhr.open("GET", "http://localhost:8080/actor");
xhr.setRequestHeader("User-Agent", "insomnia/8.4.5");
xhr.setRequestHeader("Accept-Profile", "sakila");

xhr.send(data);
```

**Kotlin**

```java
val client = OkHttpClient()

val request = Request.Builder()
  .url("http://localhost:8080/actor")
  .get()
  .addHeader("User-Agent", "insomnia/8.4.5")
  .addHeader("Accept-Profile", "sakila")
  .build()

val response = client.newCall(request).execute()
```

**Nodejs**


```javascript
const http = require("http");

const options = {
  "method": "GET",
  "hostname": "localhost",
  "port": "8080",
  "path": "/actor",
  "headers": {
    "User-Agent": "insomnia/8.4.5",
    "Accept-Profile": "sakila",
    "Content-Length": "0"
  }
};

const req = http.request(options, function (res) {
  const chunks = [];

  res.on("data", function (chunk) {
    chunks.push(chunk);
  });

  res.on("end", function () {
    const body = Buffer.concat(chunks);
    console.log(body.toString());
  });
});

req.end();

```

**PHP**

```php
<?php

$curl = curl_init();

curl_setopt_array($curl, [
  CURLOPT_PORT => "8080",
  CURLOPT_URL => "http://localhost:8080/actor",
  CURLOPT_RETURNTRANSFER => true,
  CURLOPT_ENCODING => "",
  CURLOPT_MAXREDIRS => 10,
  CURLOPT_TIMEOUT => 30,
  CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
  CURLOPT_CUSTOMREQUEST => "GET",
  CURLOPT_POSTFIELDS => "",
  CURLOPT_HTTPHEADER => [
    "Accept-Profile: sakila",
    "User-Agent: insomnia/8.4.5"
  ],
]);

$response = curl_exec($curl);
$err = curl_error($curl);

curl_close($curl);

if ($err) {
  echo "cURL Error #:" . $err;
} else {
  echo $response;
}
```

**Python**

```python
import http.client

conn = http.client.HTTPConnection("localhost:8080")

payload = ""

headers = {
    'User-Agent': "insomnia/8.4.5",
    'Accept-Profile': "sakila"
    }

conn.request("GET", "/actor", payload, headers)

res = conn.getresponse()
data = res.read()

print(data.decode("utf-8"))

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

**GO**

```GO
package main

import (
	"fmt"
	"net/http"
	"io/ioutil"
)

func main() {

	url := "http://localhost:8080/actor?select=actor_id,first_name,last_name"

	req, _ := http.NewRequest("GET", url, nil)

	req.Header.Add("User-Agent", "insomnia/8.4.5")
	req.Header.Add("Accept-Profile", "sakila")

	res, _ := http.DefaultClient.Do(req)

	defer res.Body.Close()
	body, _ := ioutil.ReadAll(res.Body)

	fmt.Println(res)
	fmt.Println(string(body))

}
```

**C#**

```csharp
var client = new RestClient("http://localhost:8080/actor?select=actor_id,first_name,last_name");
var request = new RestRequest(Method.GET);
request.AddHeader("User-Agent", "insomnia/8.4.5");
request.AddHeader("Accept-Profile", "sakila");
IRestResponse response = client.Execute(request);
```

**Java**

```java
HttpResponse<String> response = Unirest.get("http://localhost:8080/actor?select=actor_id,first_name,last_name")
  .header("User-Agent", "insomnia/8.4.5")
  .header("Accept-Profile", "sakila")
  .asString();
```

**Javascript**

```javascript
const data = null;

const xhr = new XMLHttpRequest();
xhr.withCredentials = true;

xhr.addEventListener("readystatechange", function () {
  if (this.readyState === this.DONE) {
    console.log(this.responseText);
  }
});

xhr.open("GET", "http://localhost:8080/actor?select=actor_id,first_name,last_name");
xhr.setRequestHeader("User-Agent", "insomnia/8.4.5");
xhr.setRequestHeader("Accept-Profile", "sakila");

xhr.send(data);
```

**Kotlin**

```java
val client = OkHttpClient()

val request = Request.Builder()
  .url("http://localhost:8080/actor?select=actor_id,first_name,last_name")
  .get()
  .addHeader("User-Agent", "insomnia/8.4.5")
  .addHeader("Accept-Profile", "sakila")
  .build()

val response = client.newCall(request).execute()
```

**Nodejs**


```javascript
const http = require("http");

const options = {
  "method": "GET",
  "hostname": "localhost",
  "port": "8080",
  "path": "/actor?select=actor_id,first_name,Clast_name",
  "headers": {
    "User-Agent": "insomnia/8.4.5",
    "Accept-Profile": "sakila",
    "Content-Length": "0"
  }
};

const req = http.request(options, function (res) {
  const chunks = [];

  res.on("data", function (chunk) {
    chunks.push(chunk);
  });

  res.on("end", function () {
    const body = Buffer.concat(chunks);
    console.log(body.toString());
  });
});

req.end();

```

**PHP**

```php
<?php

$curl = curl_init();

curl_setopt_array($curl, [
  CURLOPT_PORT => "8080",
  CURLOPT_URL => "http://localhost:8080/actor?select=actor_id,first_name,last_name",
  CURLOPT_RETURNTRANSFER => true,
  CURLOPT_ENCODING => "",
  CURLOPT_MAXREDIRS => 10,
  CURLOPT_TIMEOUT => 30,
  CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
  CURLOPT_CUSTOMREQUEST => "GET",
  CURLOPT_POSTFIELDS => "",
  CURLOPT_HTTPHEADER => [
    "Accept-Profile: sakila",
    "User-Agent: insomnia/8.4.5"
  ],
]);

$response = curl_exec($curl);
$err = curl_error($curl);

curl_close($curl);

if ($err) {
  echo "cURL Error #:" . $err;
} else {
  echo $response;
}
```

**Python**

```python
import http.client

conn = http.client.HTTPConnection("localhost:8080")

payload = ""

headers = {
    'User-Agent': "insomnia/8.4.5",
    'Accept-Profile': "sakila"
    }

conn.request("GET", "/actor?select=actor_id,first_name,last_name", payload, headers)

res = conn.getresponse()
data = res.read()

print(data.decode("utf-8"))

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

**GO**

```GO
package main

import (
	"fmt"
	"net/http"
	"io/ioutil"
)

func main() {

	url := "http://localhost:8080/actor?select=actor_id,first_name,last_name&filter=first_name==PENELOPE"

	req, _ := http.NewRequest("GET", url, nil)

	req.Header.Add("User-Agent", "insomnia/8.4.5")
	req.Header.Add("Accept-Profile", "sakila")

	res, _ := http.DefaultClient.Do(req)

	defer res.Body.Close()
	body, _ := ioutil.ReadAll(res.Body)

	fmt.Println(res)
	fmt.Println(string(body))

}
```

**C#**

```csharp
var client = new RestClient("http://localhost:8080/actor?select=actor_id,first_name,last_name&filter=first_name==PENELOPE");
var request = new RestRequest(Method.GET);
request.AddHeader("User-Agent", "insomnia/8.4.5");
request.AddHeader("Accept-Profile", "sakila");
IRestResponse response = client.Execute(request);
```

**Java**

```java
HttpResponse<String> response = Unirest.get("http://localhost:8080/actor?select=actor_id,first_name,last_name&filter=first_name==PENELOPE")
  .header("User-Agent", "insomnia/8.4.5")
  .header("Accept-Profile", "sakila")
  .asString();
```

**Javascript**

```javascript
const data = null;

const xhr = new XMLHttpRequest();
xhr.withCredentials = true;

xhr.addEventListener("readystatechange", function () {
  if (this.readyState === this.DONE) {
    console.log(this.responseText);
  }
});

xhr.open("GET", "http://localhost:8080/actor?select=actor_id,first_name,last_name&filter=first_name==PENELOPE");
xhr.setRequestHeader("User-Agent", "insomnia/8.4.5");
xhr.setRequestHeader("Accept-Profile", "sakila");

xhr.send(data);
```

**Kotlin**

```java
val client = OkHttpClient()

val request = Request.Builder()
  .url("http://localhost:8080/actor?select=actor_id,first_name,last_name&filter=first_name==PENELOPE")
  .get()
  .addHeader("User-Agent", "insomnia/8.4.5")
  .addHeader("Accept-Profile", "sakila")
  .build()

val response = client.newCall(request).execute()
```

**Nodejs**


```javascript
const http = require("http");

const options = {
  "method": "GET",
  "hostname": "localhost",
  "port": "8080",
  "path": "/actor?select=actor_id,first_name,last_name&filter=first_name==PENELOPE",
  "headers": {
    "User-Agent": "insomnia/8.4.5",
    "Accept-Profile": "sakila",
    "Content-Length": "0"
  }
};

const req = http.request(options, function (res) {
  const chunks = [];

  res.on("data", function (chunk) {
    chunks.push(chunk);
  });

  res.on("end", function () {
    const body = Buffer.concat(chunks);
    console.log(body.toString());
  });
});

req.end();

```

**PHP**

```php
<?php

$curl = curl_init();

curl_setopt_array($curl, [
  CURLOPT_PORT => "8080",
  CURLOPT_URL => "http://localhost:8080/actor?select=actor_id,first_name,last_name&filter=first_name==PENELOPE",
  CURLOPT_RETURNTRANSFER => true,
  CURLOPT_ENCODING => "",
  CURLOPT_MAXREDIRS => 10,
  CURLOPT_TIMEOUT => 30,
  CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
  CURLOPT_CUSTOMREQUEST => "GET",
  CURLOPT_POSTFIELDS => "",
  CURLOPT_HTTPHEADER => [
    "Accept-Profile: sakila",
    "User-Agent: insomnia/8.4.5"
  ],
]);

$response = curl_exec($curl);
$err = curl_error($curl);

curl_close($curl);

if ($err) {
  echo "cURL Error #:" . $err;
} else {
  echo $response;
}

**Python**

```python
import http.client

conn = http.client.HTTPConnection("localhost:8080")

payload = ""

headers = {
    'User-Agent': "insomnia/8.4.5",
    'Accept-Profile': "sakila"
    }

conn.request("GET", "/actor?select=actor_id,first_name,last_name&filter=first_name=="PENELOPE"", payload, headers)

res = conn.getresponse()
data = res.read()

print(data.decode("utf-8"))

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

**GO**

```GO
package main

import (
	"fmt"
	"net/http"
	"io/ioutil"
)

func main() {

	url := "http://localhost:8080/actor?select=actor_id:id,first_name:firstName,last_name:lastName"

	req, _ := http.NewRequest("GET", url, nil)

	req.Header.Add("User-Agent", "insomnia/8.4.5")
	req.Header.Add("Accept-Profile", "sakila")

	res, _ := http.DefaultClient.Do(req)

	defer res.Body.Close()
	body, _ := ioutil.ReadAll(res.Body)

	fmt.Println(res)
	fmt.Println(string(body))

}
```

**C#**

```csharp
var client = new RestClient("http://localhost:8080/actor?select=actor_id:id,first_name:firstName,last_name:lastName");
var request = new RestRequest(Method.GET);
request.AddHeader("User-Agent", "insomnia/8.4.5");
request.AddHeader("Accept-Profile", "sakila");
IRestResponse response = client.Execute(request);
```

**Java**

```java
HttpResponse<String> response = Unirest.get("http://localhost:8080/actor?select=actor_id:id,first_name:firstName,last_name:lastName")
  .header("User-Agent", "insomnia/8.4.5")
  .header("Accept-Profile", "sakila")
  .asString();
```
**Javascript**

```javascript
const data = null;

const xhr = new XMLHttpRequest();
xhr.withCredentials = true;

xhr.addEventListener("readystatechange", function () {
  if (this.readyState === this.DONE) {
    console.log(this.responseText);
  }
});

xhr.open("GET", "http://localhost:8080/actor?select=actor_id:id,first_name:firstName,last_name:lastName");
xhr.setRequestHeader("User-Agent", "insomnia/8.4.5");
xhr.setRequestHeader("Accept-Profile", "sakila");

xhr.send(data);
```

**Kotlin**

```java
val client = OkHttpClient()

val request = Request.Builder()
  .url("http://localhost:8080/actor?select=actor_id:id,first_name:firstName,last_name:lastName")
  .get()
  .addHeader("User-Agent", "insomnia/8.4.5")
  .addHeader("Accept-Profile", "sakila")
  .build()

val response = client.newCall(request).execute()
```

**Nodejs**


```javascript
const http = require("http");

const options = {
  "method": "GET",
  "hostname": "localhost",
  "port": "8080",
  "path": "/actor?select=actor_id:id,first_name:firstName,last_name:lastName",
  "headers": {
    "User-Agent": "insomnia/8.4.5",
    "Accept-Profile": "sakila",
    "Content-Length": "0"
  }
};

const req = http.request(options, function (res) {
  const chunks = [];

  res.on("data", function (chunk) {
    chunks.push(chunk);
  });

  res.on("end", function () {
    const body = Buffer.concat(chunks);
    console.log(body.toString());
  });
});

req.end();

```

**PHP**

```php
<?php

$curl = curl_init();

curl_setopt_array($curl, [
  CURLOPT_PORT => "8080",
  CURLOPT_URL => "http://localhost:8080/actor?select=actor_id:id,first_name:firstName,last_name:lastName",
  CURLOPT_RETURNTRANSFER => true,
  CURLOPT_ENCODING => "",
  CURLOPT_MAXREDIRS => 10,
  CURLOPT_TIMEOUT => 30,
  CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
  CURLOPT_CUSTOMREQUEST => "GET",
  CURLOPT_POSTFIELDS => "",
  CURLOPT_HTTPHEADER => [
    "Accept-Profile: sakila",
    "User-Agent: insomnia/8.4.5"
  ],
]);

$response = curl_exec($curl);
$err = curl_error($curl);

curl_close($curl);

if ($err) {
  echo "cURL Error #:" . $err;
} else {
  echo $response;
}
```

**Python**

```python
import http.client

conn = http.client.HTTPConnection("localhost:8080")

payload = ""

headers = {
    'User-Agent': "insomnia/8.4.5",
    'Accept-Profile': "sakila"
    }

conn.request("GET", "/actor?select=actor_id:id,first_name:firstName,last_name:lastName", payload, headers)

res = conn.getresponse()
data = res.read()

print(data.decode("utf-8"))

```



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


