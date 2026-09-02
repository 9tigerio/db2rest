package com.homihq.db2rest.jdbc.processor;

import com.db2rest.jdbc.dialect.model.DbColumn;
import com.homihq.db2rest.jdbc.dto.ReadContext;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.List;

import com.db2rest.jdbc.dialect.model.DbTable;

class PrimaryKeyProcessorTest {
    private DbColumn pkColumn(String name) {
        return new DbColumn("table", name, null, null, true, null, false, false, String.class, null, null);
    }

    @Test
    void shouldCreateFilterForSinglePrimaryKey() {
        ReadContext context = new ReadContext();
        context.setPrimaryKey("1");

        DbTable table = mock(DbTable.class);

        when(table.buildPkColumns())
                .thenReturn(List.of(
                        pkColumn("id")
                ));

        context.setRoot(table);

        new PrimaryKeyProcessor().process(context);

        assertEquals("id==1", context.getFilter());
    }

    @Test
    void shouldCreateFilterForMultiplePrimaryKeys() {
        ReadContext context = new ReadContext();
        context.setPrimaryKey("1,2");

        DbTable table = mock(DbTable.class);

        when(table.buildPkColumns())
                .thenReturn(List.of(
                        pkColumn("id"),
                        pkColumn("type")
                ));

        context.setRoot(table);

        new PrimaryKeyProcessor().process(context);

        assertEquals("id==1;type==2", context.getFilter());
    }

    @Test
    void shouldMergePrimaryKeyWithExistingFilter() {
        ReadContext context = new ReadContext();
        context.setPrimaryKey("1,2");
        context.setFilter("comment==true");

        DbTable table = mock(DbTable.class);

        when(table.buildPkColumns())
                .thenReturn(List.of(
                        pkColumn("id"),
                        pkColumn("type")
                ));

        context.setRoot(table);

        new PrimaryKeyProcessor().process(context);

        assertEquals("id==1;type==2;comment==true", context.getFilter());
    }
}