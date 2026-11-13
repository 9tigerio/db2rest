package com.homihq.db2rest.jdbc;

import com.db2rest.jdbc.dialect.model.ArrayTypeValueHolder;
import com.homihq.db2rest.core.exception.GenericDataAccessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.SqlValue;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Creates an array on the statement's connection and releases it before Spring
 * returns that connection. A value belongs to one create operation only.
 */
@Slf4j
final class JdbcArrayValue implements SqlValue {

    private final ArrayTypeValueHolder value;
    private Array array;

    JdbcArrayValue(ArrayTypeValueHolder value) {
        this.value = value;
    }

    @Override
    public void setValue(PreparedStatement statement, int parameterIndex) {
        try {
            // A repeated named parameter can be bound more than once.
            if (array == null) {
                array = statement.getConnection().createArrayOf(value.sqlType(), value.values());
            }
            statement.setArray(parameterIndex, array);
        } catch (SQLException exception) {
            GenericDataAccessException failure = new GenericDataAccessException("Unable to convert Array field");
            failure.initCause(exception);
            throw failure;
        }
    }

    @Override
    public void cleanup() {
        Array toFree = array;
        array = null;
        if (toFree != null) {
            try {
                toFree.free();
            } catch (SQLException exception) {
                // Spring closes the statement and releases the connection after
                // parameter cleanup. Never prevent those steps or mask a failure.
                log.warn("Unable to release JDBC array value.");
            }
        }
    }
}
