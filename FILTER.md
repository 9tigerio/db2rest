# Filter Design V2 (!!!NOT USED!!!)


## Introduction

Filtering is the process of limiting a collection resource by using a per-request dynamic criteria definition.Filtering enables efficient traversal of large collections.
Filtering can also be backed with pagination where each page contains a subset of items found in the complete collection.

To filter in a query, include the parameter q=QueryObject, where QueryObject is a JSON object that represents the custom selection, pagination and sorting to be applied to the resource. 

For example, assume the following resource:

```ruby
https://db2rest.com/actors
```

The following query includes a filter that restricts actors with first_name column to "Robert"

```ruby
https://db2rest.com/actors?q={"first_name":"Robert"}
```

## QueryObject Grammer

**EQUALS operator ($eq)**

(Implicit and explicit equality supported.)

Implicit (Support String and Dates too)

```ruby
https://db2rest.com/actors?q={"year": 1999}
```

Explicit

```ruby
https://db2rest.com/actors?q={"year": {"$eq": 1000} }
```

Strings

```ruby
https://db2rest.com/actors?q={"first_name": {"$eq": "Robert"} }
```

Dates

```ruby
https://db2rest.com/actors?q={"date_of_birth": {"$date": "1981-11-17T08:00:00Z"} }
```


**NOT EQUALS operator ($ne)**

Number

```ruby
{"budget": {"$ne": 1000000}}
```

String

```ruby
{"last_name": {"$ne":"De Niro"}}
```

Dates

```ruby
{"date_of_birth": {"$ne": {"$date":"1981-11-17T08:00:00Z"}}}
```


**LESS THAN operator ($lt)**
(Supports dates and numbers only)

Numbers

```ruby
{"budget": {"$lt": 100000000} }
```

Dates

```ruby
{"date_of_release": {"$lt": {"$date":"1999-12-17T08:00:00Z"}}}
```

**LESS THAN OR EQUALS operator ($lte)**
(Supports dates and numbers only)

Numbers

```ruby
{"budget": {"$lte": 100000000}}
```

Dates

```ruby
{ "date_of_birth": {"$lte": {"$date":"1999-12-17T08:00:00Z"}} }
```

**GREATER THAN operator ($gt)**
(Supports dates and numbers only)

Numbers

```ruby
{ "budget": {"$gt": 10000} }
```

Dates

```ruby
{ "date_of_birth": {"$gt": {"$date":"1999-12-17T08:00:00Z"}} }
```

**GREATER THAN OR EQUALS operator ($gte)**
(Supports dates and numbers only)

Numbers

```ruby
{"budget": {"$gte": 10000}}
```

Dates

```ruby
{"date_of_birth": {"$gte": {"$date":"1999-12-17T08:00:00Z"}} }
```

**In string operator ($instr)**
(Supports strings only)

```ruby
{"first_name": {"$instr":"MC"}}
```

Not in string operator ($ninstr)
(Supports strings only)

```ruby
{"first_name": {"$ninstr":"MC"}}
```


#### LIKE operator ($like)
(Supports strings. Eescape character not supported to try to match expressions with _ or % characters.)

```ruby
{"first_name": {"$like":"AX%"}}
```

#### BETWEEN operator ($between)
(Supports string, dates, and numbers)

Numbers

```ruby
{"budget": {"$between": [1000,2000]}}
```
Dates

```ruby
{"release_date": {"$between": [{"$date":"1989-12-17T08:00:00Z"},{"$date":"1999-12-17T08:00:00Z"}]}}
```

Strings

```ruby
{"first_name": {"$between": ["A","C"]}}
```

**Null Ranges ($lte equivalent)**
(Supported by numbers and dates only)

```ruby
{"budget": {"$between": [null,2000]}}
```

Null Ranges ($gte equivalent)
(Supported by numbers and dates only)

```ruby
{"budget": {"$between": [1000,null]}}
```

#### NULL operator ($null)

```ruby
{"first_name": {"$null": null}}
```

#### NOT NULL operator ($notnull)

```ruby
{"first_name": {"$notnull": null}}
```


#### AND operator ($and)
(Supports all operators, including $and and $or)

Column context delegation
(Operators inside $and will use the closest context defined in the JSON tree.)

```ruby
{"budget": {"$and": [{"$gt": 1000},{"$lt":4000}]}}
```

Column context override
(Example: salary greater than 1000 and name like S%)

```ruby
{
"SALARY": {"$and": [{"$gt": 1000},{"ENAME": {"$like":"S%"}} ] }
}
```

Implicit and in columns

```ruby
{"budget": [{"$gt": 1000},{"$lt":4000}]}
```

High order AND
(All first columns and or high order operators -- $and and $ors -- defined at the first level of the JSON will be joined and an implicit AND)
(Example: Salary greater than 1000 and name starts with S or T)

```ruby
{   "budget": {"$gt": 1000},"first_name": {"$or": [{"$like":"S%"}, {"$like":"T%"}]}}
```

Invalid expression (operators $lt and $gt lack column context)

```ruby
{"$and": [{"$lt": 5000},{"$gt": 1000}]}
```

Valid alternatives for the previous invalid expression

```ruby
{"$and": [{"budget": {"$lt": 5000}}, {"budget": {"$gt": 1000}}]}
```

```ruby
{"budget": [{"$lt": 5000},{"$gt": 1000}]}
```

```ruby
{"budget": {"$and": [{"$lt": 5000},{"$gt": 1000}]}}
```

#### OR operator ($or)
(Supports all operators including $and and $or)

Column context delegation
(Operators inside $or will use the closest context defined in the JSON tree)

```ruby
{"budget": {"$or": [{"$eq":"SMITH"},{"$eq":"KING"}]} }
```

Column context override
(Example: name starts with S or salary greater than 1000)

```ruby
{
"budget": {"$or": [{"$gt": 1000},{"last_name": {"$like":"S%"}} ] }
}
```
