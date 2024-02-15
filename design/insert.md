# Insert a single record

1. Construct INSERT columns with the key set of the incoming data object. 
2. If `includeColumns` is specified then use those instead of #1 
3. If a column with default value is not included in #1 or #2 above then it will be set to the default value by the database.
4. If `tsIdEnabed = true`, then find the primary key columns.
   - detect the data type - if integer type, set a numeric TSID. 
   - if the data type is string - set string type TSID.
   - add these columns in the data. 

# Bulk insert 

**All the rows or documents must have the same structure**


1. Construct INSERT columns with the key set of the incoming data object. Use the first entry (assumption same structure)
2. If `includeColumns` is specified then use those instead of #1
3. If a column with default value is not included in #1 or #2 above then it will be set to the default value by the database.
4. If `tsIdEnabed = true`, then find the primary key columns. (apply for all rows of data)
   - detect the data type 
     - if integer type, create  numeric TSID.
     - if the data type is string - set string type TSID.
   - add these new values (with column name as key) in the incoming dataset. 
