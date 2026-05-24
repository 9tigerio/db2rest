package com.homihq.db2rest.jdbc.sql;

import com.db2rest.jdbc.dialect.model.DbTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostgreSQLDataExclusionTest {

    @Mock
    private DatabaseMetaData databaseMetaData;

    @Mock
    private ResultSet tablesResultSet;

    @Mock
    private ResultSet primaryKeysResultSet;

    @Mock
    private ResultSet columnsResultSet;

    @Test
    void shouldQuoteSchemaAndTableNameInFullName() throws SQLException {
        PostgreSQLDataExclusion dataExtraction = new PostgreSQLDataExclusion();
        String schemaName = "0HEM8B0GQNB5M";
        String tableName = "t_region";

        when(databaseMetaData.getTables(isNull(), eq(schemaName), isNull(), any(String[].class)))
                .thenReturn(tablesResultSet);
        when(tablesResultSet.next()).thenReturn(true, false);
        when(tablesResultSet.getString(ColumnLabel.TABLE_NAME.name())).thenReturn(tableName);
        when(tablesResultSet.getString(ColumnLabel.TABLE_CAT.name())).thenReturn(null);
        when(tablesResultSet.getString(ColumnLabel.TABLE_SCHEM.name())).thenReturn(schemaName);
        when(tablesResultSet.getString(ColumnLabel.TABLE_TYPE.name())).thenReturn("TABLE");

        when(databaseMetaData.getPrimaryKeys(isNull(), eq(schemaName), eq(tableName)))
                .thenReturn(primaryKeysResultSet);
        when(primaryKeysResultSet.next()).thenReturn(false);

        when(databaseMetaData.getColumns(isNull(), eq(schemaName), eq(tableName), isNull()))
                .thenReturn(columnsResultSet);
        when(columnsResultSet.next()).thenReturn(true, false);
        when(columnsResultSet.getString(ColumnLabel.COLUMN_NAME.name())).thenReturn("id");
        when(columnsResultSet.getInt(ColumnLabel.DATA_TYPE.name())).thenReturn(4);
        when(columnsResultSet.getString(ColumnLabel.IS_AUTOINCREMENT.name())).thenReturn("NO");
        when(columnsResultSet.getString(ColumnLabel.TYPE_NAME.name())).thenReturn("int4");

        List<DbTable> dbTables = dataExtraction.getTables(databaseMetaData, false, List.of(schemaName));

        assertThat(dbTables).singleElement()
                .extracting(DbTable::fullName)
                .isEqualTo("\"0HEM8B0GQNB5M\".\"t_region\"");
    }
}
