# Filter v2 Design


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


