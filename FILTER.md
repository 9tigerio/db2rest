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
