package com.homihq.db2rest.rest.read.helper;

import com.homihq.db2rest.rest.read.model.DbAlias;
import com.homihq.db2rest.rest.read.model.DbColumn;
import com.homihq.db2rest.rest.read.model.DbTable;

@Deprecated
public abstract class TableUtil {

    private TableUtil() {}


    @Deprecated
    public static DbTable getTable(String tableName) {
        //check if table contains schema name

        String [] tableParts = tableName.split("!.");

        if(tableParts.length == 2) {
            String table = tableParts[1];
            DbAlias alias = getAlias(table);
           // return new DbTable(tableParts[0], alias.name(), alias.alias());
            return null;
        }
        else{
            String table = tableParts[0];
            DbAlias alias = getAlias(table);
            //return new DbTable(null, alias.name(), alias.alias());
            return null;
        }
    }

    @Deprecated
    public static DbColumn getColumn(String columName) {
        //check if table contains schema name

        String [] columnParts = columName.split("!.");

        if(columnParts.length == 2) {
            String col = columnParts[1];
            DbAlias alias = getAlias(col);
            //return new DbColumn(columnParts[0], alias.name(), alias.alias());
            return null;
        }
        else{
            String col = columnParts[0];
            DbAlias alias = getAlias(col);
            //return new DbColumn(null, alias.name(), alias.alias());
            return null;
        }
    }

    public static DbAlias getAlias(String name) {
        String [] aliasParts = name.split(":");

        if(aliasParts.length == 2) {
            return new DbAlias(aliasParts[0], aliasParts[1]);
        }
        else {
            return new DbAlias(aliasParts[0], null);
        }
    }
}
