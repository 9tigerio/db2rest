package com.homihq.db2rest.jdbc.rsql.resolver;

import com.db2rest.jdbc.dialect.model.DbColumn;
import com.db2rest.jdbc.dialect.model.DbTable;
import com.homihq.db2rest.core.exception.InvalidColumnException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * Resolves column references across multiple tables in join operations.
 * Handles column selectors like "tops.color" and "bottoms.color" by finding
 * the appropriate table based on the table prefix.
 */
@Slf4j
public class CrossTableColumnResolver {

    /**
     * Resolves a column reference that may include a table prefix.
     * 
     * @param columnSelector The column selector (e.g., "tops.color", "color", "bottoms.size")
     * @param allTables List of all tables involved in the query (root + joined tables)
     * @param fallbackTable The fallback table to use if no table prefix is found
     * @return The resolved DbColumn
     * @throws InvalidColumnException if the column cannot be resolved
     */
    public static DbColumn resolveColumn(String columnSelector, List<DbTable> allTables, DbTable fallbackTable) {
        log.debug("Resolving column selector: {} across {} tables", columnSelector, 
                  allTables != null ? allTables.size() : 0);
        
        if (StringUtils.isBlank(columnSelector)) {
            throw new InvalidColumnException("", "Column selector cannot be blank");
        }
        
        // Check if the column selector contains a table prefix (e.g., "tops.color")
        if (columnSelector.contains(".")) {
            String[] parts = columnSelector.split("\\.", 2);
            if (parts.length == 2) {
                String tablePrefix = parts[0];
                String columnName = parts[1];
                
                log.debug("Column selector has table prefix: {} for column: {}", tablePrefix, columnName);
                
                // Find the table that matches the prefix
                DbTable targetTable = findTableByPrefix(tablePrefix, allTables);
                if (targetTable != null) {
                    log.debug("Found target table: {} for prefix: {}", targetTable.name(), tablePrefix);
                    return targetTable.buildColumn(columnName);
                } else {
                    log.warn("No table found for prefix: {}, falling back to default table", tablePrefix);
                }
            }
        }
        
        // If no table prefix found or table not found, use the fallback table
        log.debug("Using fallback table: {} for column: {}", 
                  fallbackTable != null ? fallbackTable.name() : "null", columnSelector);
        
        if (fallbackTable == null) {
            throw new InvalidColumnException("", "No fallback table available for column: " + columnSelector);
        }
        
        return fallbackTable.buildColumn(columnSelector);
    }
    
    /**
     * Finds a table that matches the given prefix.
     * The prefix can match either the table name or the table alias.
     * 
     * @param prefix The table prefix to search for
     * @param allTables List of all available tables
     * @return The matching DbTable or null if not found
     */
    private static DbTable findTableByPrefix(String prefix, List<DbTable> allTables) {
        if (Objects.isNull(allTables) || allTables.isEmpty()) {
            log.debug("No tables available for prefix matching");
            return null;
        }
        
        for (DbTable table : allTables) {
            // Check if prefix matches table name (case-insensitive)
            if (StringUtils.equalsIgnoreCase(prefix, table.name())) {
                log.debug("Found table by name match: {} for prefix: {}", table.name(), prefix);
                return table;
            }
            
            // Check if prefix matches table alias (case-insensitive)
            if (StringUtils.isNotBlank(table.alias()) && 
                StringUtils.equalsIgnoreCase(prefix, table.alias())) {
                log.debug("Found table by alias match: {} (alias: {}) for prefix: {}", 
                         table.name(), table.alias(), prefix);
                return table;
            }
        }
        
        log.debug("No table found for prefix: {}", prefix);
        return null;
    }
}
