package com.homihq.db2rest.jdbc;

import com.db2rest.jdbc.dialect.model.ArrayTypeValueHolder;
import com.homihq.db2rest.core.exception.GenericDataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class JdbcOperationServiceArrayTest {

    private final JdbcOperationService service = new JdbcOperationService();
    private DataSource dataSource;
    private Connection connection;
    private PreparedStatement statement;
    private Array array;
    private NamedParameterJdbcTemplate jdbc;
    private final Object[] elements = {"one", "two"};

    @BeforeEach
    void setUp() throws Exception {
        dataSource = mock(DataSource.class);
        connection = mock(Connection.class);
        statement = mock(PreparedStatement.class);
        array = mock(Array.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(true);
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(statement);
        when(statement.getConnection()).thenReturn(connection);
        when(statement.executeUpdate()).thenReturn(1);
        when(statement.getGeneratedKeys()).thenReturn(mock(ResultSet.class));
        when(connection.createArrayOf("varchar", elements)).thenReturn(array);
        jdbc = new NamedParameterJdbcTemplate(dataSource);
    }

    @Test
    void nonTransactionalInsertBorrowsOneConnectionAndFreesArrayBeforeReturningIt() throws Exception {
        assertEquals(1, insert().row());
        verify(dataSource, times(1)).getConnection();
        var order = inOrder(statement, array, connection);
        order.verify(statement).executeUpdate();
        order.verify(array).free();
        order.verify(connection).close();
    }

    @Test
    void transactionalInsertUsesItsBoundConnectionAndCommits() throws Exception {
        TransactionTemplate transaction = new TransactionTemplate(new JdbcTransactionManager(dataSource));
        assertEquals(1, transaction.execute(status -> insert()).row());
        verify(dataSource, times(1)).getConnection();
        verify(statement).getConnection();
        verify(connection).createArrayOf("varchar", elements);
        verify(array).free();
        verify(connection).commit();
        verify(connection).close();
    }

    @Test
    void conversionFailureReturnsSanitizedErrorAndRollsBack() throws Exception {
        when(connection.createArrayOf("varchar", elements))
                .thenThrow(new SQLException("private-db-host secret-token", "22000"));
        TransactionTemplate transaction = new TransactionTemplate(new JdbcTransactionManager(dataSource));
        GenericDataAccessException failure = assertThrows(GenericDataAccessException.class,
                () -> transaction.execute(status -> insert()));
        assertEquals("Unable to convert Array field", failure.getMessage());
        verify(statement, never()).executeUpdate();
        verify(connection).rollback();
        verify(connection).close();
        verify(dataSource, times(1)).getConnection();
    }

    @Test
    void unsupportedDriverDoesNotLeakAConnection() throws Exception {
        when(connection.createArrayOf("varchar", elements)).thenThrow(new SQLFeatureNotSupportedException());
        assertThrows(GenericDataAccessException.class, this::insert);
        verify(connection).close();
        verify(statement, never()).executeUpdate();
    }

    @Test
    void bindingFailureFreesTheCreatedArrayAndClosesConnection() throws Exception {
        doThrow(new SQLException("private binding details", "22000")).when(statement).setArray(1, array);
        assertThrows(GenericDataAccessException.class, this::insert);
        verify(array).free();
        verify(connection).close();
        verify(statement, never()).executeUpdate();
    }

    @Test
    void executionFailureStillFreesArrayAndRollsBack() throws Exception {
        when(statement.executeUpdate()).thenThrow(new SQLException("duplicate key", "23505"));
        TransactionTemplate transaction = new TransactionTemplate(new JdbcTransactionManager(dataSource));
        assertThrows(RuntimeException.class, () -> transaction.execute(status -> insert()));
        verify(array).free();
        verify(connection).rollback();
        verify(connection).close();
    }

    @Test
    void repeatedNamedParameterReusesAndFreesItsArrayOnce() throws Exception {
        service.create(jdbc, Map.of("tags", holder()),
                "INSERT INTO example (tags, backup_tags) VALUES (:tags, :tags)", null);
        verify(connection, times(1)).createArrayOf("varchar", elements);
        verify(statement).setArray(1, array);
        verify(statement).setArray(2, array);
        verify(array, times(1)).free();
    }

    @Test
    void laterArrayConversionFailureFreesEarlierArray() throws Exception {
        Object[] invalid = {"invalid"};
        when(connection.createArrayOf("varchar", invalid)).thenThrow(new SQLException("conversion failure"));
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("first", holder());
        data.put("second", new ArrayTypeValueHolder("java.sql.Array", "varchar", invalid));
        assertThrows(GenericDataAccessException.class, () -> service.create(jdbc, data,
                "INSERT INTO example (tags, backup_tags) VALUES (:first, :second)", null));
        verify(array).free();
        verify(connection).close();
        verify(statement, never()).executeUpdate();
    }

    @Test
    void arrayCleanupFailureCannotPreventConnectionRelease() throws Exception {
        doThrow(new SQLException("private driver cleanup details")).when(array).free();
        assertEquals(1, insert().row());
        verify(array).free();
        verify(statement).close();
        verify(connection).close();
    }

    @Test
    void scalarInsertKeepsExistingBehavior() throws Exception {
        assertEquals(1, service.create(jdbc, Map.of("name", "example"),
                "INSERT INTO example (name) VALUES (:name)", null).row());
        verify(connection, never()).createArrayOf(anyString(), any(Object[].class));
        verify(statement).setString(1, "example");
        verify(connection).close();
    }

    private com.homihq.db2rest.core.dto.CreateResponse insert() {
        return service.create(jdbc, Map.of("tags", holder()),
                "INSERT INTO example (tags) VALUES (:tags)", null);
    }

    private ArrayTypeValueHolder holder() {
        return new ArrayTypeValueHolder("java.sql.Array", "varchar", elements);
    }
}
