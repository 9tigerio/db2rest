package com.homihq.db2rest.d1;

import com.homihq.db2rest.d1.model.D1Column;
import com.homihq.db2rest.d1.model.D1Table;
import com.homihq.db2rest.core.model.DbColumn;
import com.homihq.db2rest.core.model.DbTable;
import static com.homihq.db2rest.schema.AliasGenerator.getAlias;
import com.homihq.db2rest.schema.SchemaCache;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class D1SchemaCache implements SchemaCache {

    private final D1RestClient d1RestClient;
    private List<DbTable> dbTableList;
    private Map<String,DbTable> dbTableMap;

    @PostConstruct
    private void reload() {
        createSchemaCache();
    }

    private void createSchemaCache() {
        dbTableList = new ArrayList<>();
        dbTableMap = new HashMap<>();
        
        List<D1Table> d1Tables =
        d1RestClient.getMetaDataAllTables();

        //now for each table get column info
        for(D1Table d1Table : d1Tables) {
            log.info("D1 Table - {}", d1Table);
            if(StringUtils.startsWith(d1Table.name(), "_cf")) continue;
            List<D1Column> d1Columns = d1RestClient.getMetaDataAllColumns(d1Table.name());

            log.info("D1 Columns - {}", d1Columns);
            addTableWithColumns(d1Table, d1Columns);
        }
    }

    private void addTableWithColumns(D1Table d1Table, List<D1Column> d1Columns) {
        String tableAlias = getAlias(d1Table.name());
        List<DbColumn> dbColumns = 
                d1Columns.stream()
                        .map(d1Column -> new DbColumn(
                                d1Table.name(),
                                d1Column.name(),
                                "",
                                tableAlias,
                                d1Column.pk(),
                                d1Column.type(),
                                false,
                                false, 
                                getTypedClass(d1Column.type())
                        )).toList();
        
        DbTable dbTable = new DbTable(d1Table.schema()
            , d1Table.name(), d1Table.name(), tableAlias,dbColumns, null);

        this.dbTableList.add(dbTable);
        this.dbTableMap.put(d1Table.name(), dbTable);
    }

    private Class<?> getTypedClass(String type) { //TODO supported by dialect
        if(StringUtils.equalsIgnoreCase("INTEGER", type)) {
            return Long.class;
        }
        else if(StringUtils.equalsIgnoreCase("REAL", type)) {
            return Double.class;
        }
        else {
            return String.class;
        }

    }

    @Override
    public DbTable getTable(String tableName) {
        return this.dbTableMap.get(tableName);
    }



}
