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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
        PostgreSQLDataExclusion dataExclusion = new PostgreSQLDataExclusion();
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

        List<DbTable> dbTables = dataExclusion.getTables(databaseMetaData, false, List.of(schemaName));

        assertThat(dbTables).singleElement()
                .extracting(DbTable::fullName)
                .isEqualTo("\"0HEM8B0GQNB5M\".\"t_region\"");
    }

    @Test
    void shouldQuoteUnicodeSchemaAndTableNameInFullName() throws SQLException {
        PostgreSQLDataExclusion dataExclusion = new PostgreSQLDataExclusion();
        String schemaName = "dátá_数据";
        String tableName = "dátá";

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

        List<DbTable> dbTables = dataExclusion.getTables(databaseMetaData, false, List.of(schemaName));

        assertThat(dbTables).singleElement()
                .extracting(DbTable::fullName)
                .isEqualTo("\"dátá_数据\".\"dátá\"");
    }

    @Test
    void shouldRequestSystemTableTypesForExplicitlyIncludedSystemSchema() throws SQLException {
        PostgreSQLDataExclusion dataExclusion = new PostgreSQLDataExclusion();
        String[] systemTypes = {"TABLE", "VIEW", "SYSTEM TABLE", "SYSTEM VIEW"};

        when(databaseMetaData.getTables(isNull(), eq("pg_catalog"), isNull(), eq(systemTypes)))
                .thenReturn(tablesResultSet);
        when(tablesResultSet.next()).thenReturn(true, false);
        when(tablesResultSet.getString(ColumnLabel.TABLE_NAME.name())).thenReturn("pg_class");
        when(tablesResultSet.getString(ColumnLabel.TABLE_CAT.name())).thenReturn(null);
        when(tablesResultSet.getString(ColumnLabel.TABLE_SCHEM.name())).thenReturn("pg_catalog");
        when(tablesResultSet.getString(ColumnLabel.TABLE_TYPE.name())).thenReturn("SYSTEM TABLE");

        when(databaseMetaData.getPrimaryKeys(isNull(), eq("pg_catalog"), eq("pg_class")))
                .thenReturn(primaryKeysResultSet);
        when(primaryKeysResultSet.next()).thenReturn(false);
        when(databaseMetaData.getColumns(isNull(), eq("pg_catalog"), eq("pg_class"), isNull()))
                .thenReturn(columnsResultSet);
        when(columnsResultSet.next()).thenReturn(false);

        List<DbTable> dbTables = dataExclusion.getTables(databaseMetaData, false, List.of("pg_catalog"));

        assertThat(dbTables).singleElement()
                .extracting(DbTable::fullName)
                .isEqualTo("\"pg_catalog\".\"pg_class\"");
    }

    @Test
    void shouldRequestOnlyRegularTableTypesForUserSchema() throws SQLException {
        PostgreSQLDataExclusion dataExclusion = new PostgreSQLDataExclusion();

        when(databaseMetaData.getTables(isNull(), eq("public"), isNull(), eq(new String[] {"TABLE", "VIEW"})))
                .thenReturn(tablesResultSet);
        when(tablesResultSet.next()).thenReturn(false);

        assertThat(dataExclusion.getTables(databaseMetaData, false, List.of("public"))).isEmpty();
    }

    @Test
    void shouldStillSkipSystemSchemasWhenLoadingAllSchemas() throws SQLException {
        PostgreSQLDataExclusion dataExclusion = new PostgreSQLDataExclusion();
        ResultSet schemas = org.mockito.Mockito.mock(ResultSet.class);

        when(databaseMetaData.getSchemas()).thenReturn(schemas);
        when(schemas.next()).thenReturn(true, true, true, false);
        when(schemas.getString(ColumnLabel.TABLE_SCHEM.name()))
                .thenReturn("pg_catalog", "information_schema", "public");
        when(databaseMetaData.getTables(isNull(), eq("public"), isNull(), any(String[].class)))
                .thenReturn(tablesResultSet);
        when(tablesResultSet.next()).thenReturn(false);

        dataExclusion.getTables(databaseMetaData, true, List.of());

        verify(databaseMetaData, never()).getTables(isNull(), eq("pg_catalog"), isNull(), any(String[].class));
        verify(databaseMetaData, never()).getTables(isNull(), eq("information_schema"), isNull(), any(String[].class));
    }

    @Test
    void shouldSkipTablesOutsideTheTableFilterWithoutReadingTheirColumns() throws SQLException {
        PostgreSQLDataExclusion dataExclusion = new PostgreSQLDataExclusion();

        when(databaseMetaData.getTables(isNull(), eq("public"), isNull(), any(String[].class)))
                .thenReturn(tablesResultSet);
        when(tablesResultSet.next()).thenReturn(true, true, false);
        when(tablesResultSet.getString(ColumnLabel.TABLE_NAME.name())).thenReturn("film", "customer");
        when(tablesResultSet.getString(ColumnLabel.TABLE_CAT.name())).thenReturn(null);
        when(tablesResultSet.getString(ColumnLabel.TABLE_SCHEM.name())).thenReturn("public");
        when(tablesResultSet.getString(ColumnLabel.TABLE_TYPE.name())).thenReturn("TABLE");

        when(databaseMetaData.getPrimaryKeys(isNull(), eq("public"), eq("film")))
                .thenReturn(primaryKeysResultSet);
        when(primaryKeysResultSet.next()).thenReturn(false);
        when(databaseMetaData.getColumns(isNull(), eq("public"), eq("film"), isNull()))
                .thenReturn(columnsResultSet);
        when(columnsResultSet.next()).thenReturn(false);

        List<DbTable> dbTables = dataExclusion.getTables(databaseMetaData, false, List.of("public"),
                TableFilter.of(List.of("film*")));

        assertThat(dbTables).extracting(DbTable::name).containsExactly("film");
        verify(databaseMetaData, never()).getColumns(any(), any(), eq("customer"), any());
        verify(databaseMetaData, never()).getPrimaryKeys(any(), any(), eq("customer"));
    }
}
