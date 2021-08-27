package com.aconex.scrutineer2.jdbc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.aconex.scrutineer2.IdAndVersionFactory;
import com.aconex.scrutineer2.StringIdAndVersion;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class JdbcIdAndVersionStreamTest {

    private static final String SQL = "select id, version from tablename order by id";

	private IdAndVersionFactory idAndVersionFactory = StringIdAndVersion.FACTORY;

    @Mock
    private Connection connection;
    @Mock
    private Statement statement;
    @Mock
    private ResultSet resultSet;
    @Mock
    private ResultSetMetaData metaData;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void shouldCloseDBResourcesEvenIfNoIteration() throws SQLException {
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(SQL)).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(metaData);

        JdbcIdAndVersionStream jdbcIdAndVersionStream = new JdbcIdAndVersionStream(connection, SQL, idAndVersionFactory);
        jdbcIdAndVersionStream.open();
        jdbcIdAndVersionStream.close();

        verify(statement).close();
        verify(resultSet).close();
        verify(connection, never()).close();
    }

    @Test
    public void closeShouldDoNothingIfNotOpen() {
        JdbcIdAndVersionStream jdbcIdAndVersionStream = new JdbcIdAndVersionStream(connection, SQL, idAndVersionFactory);
        jdbcIdAndVersionStream.close();
        verifyNoMoreInteractions(connection);
    }

    @Test
    public void shouldExecuteSQLQuery() throws SQLException {
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(SQL)).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(metaData);
        JdbcIdAndVersionStream jdbcIdAndVersionStream = new JdbcIdAndVersionStream(connection, SQL, idAndVersionFactory);
        jdbcIdAndVersionStream.open();
        IdAndVersionResultSetIterator iterator = (IdAndVersionResultSetIterator) jdbcIdAndVersionStream.iterator();
        assertThat(iterator.getResultSet(), Is.is(resultSet));
        verify(statement).executeQuery(SQL);
    }

    @Test
    public void shouldTearDownStatement() throws SQLException {
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(SQL)).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(metaData);
        JdbcIdAndVersionStream jdbcIdAndVersionStream = new JdbcIdAndVersionStream(connection, SQL, idAndVersionFactory);
        jdbcIdAndVersionStream.open();
        jdbcIdAndVersionStream.iterator();
        jdbcIdAndVersionStream.close();
        verify(resultSet).close();
        verify(statement).close();
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionIfResultSetFailsToClose() throws SQLException {
        try {
            when(connection.createStatement()).thenReturn(statement);
            when(statement.executeQuery(SQL)).thenReturn(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Unexpected exception");
        }
        doThrow(new SQLException()).when(resultSet).close();
        JdbcIdAndVersionStream jdbcIdAndVersionStream = new JdbcIdAndVersionStream(connection, SQL, idAndVersionFactory);
        jdbcIdAndVersionStream.open();
        jdbcIdAndVersionStream.iterator();
        jdbcIdAndVersionStream.close();
    }
}
