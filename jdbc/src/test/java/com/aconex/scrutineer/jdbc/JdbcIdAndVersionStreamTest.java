package com.aconex.scrutineer.jdbc;

import static org.hamcrest.MatcherAssert.assertThat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.aconex.scrutineer.IdAndVersionFactory;
import com.aconex.scrutineer.StringIdAndVersion;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
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
        Mockito.when(connection.createStatement()).thenReturn(statement);
        Mockito.when(statement.executeQuery(SQL)).thenReturn(resultSet);
        Mockito.when(resultSet.getMetaData()).thenReturn(metaData);

        JdbcIdAndVersionStream jdbcIdAndVersionStream = new JdbcIdAndVersionStream(connection, SQL, idAndVersionFactory);
        jdbcIdAndVersionStream.open();
        jdbcIdAndVersionStream.close();

        Mockito.verify(statement).close();
        Mockito.verify(resultSet).close();
        Mockito.verify(connection, Mockito.never()).close();
    }

    @Test
    public void closeShouldDoNothingIfNotOpen() throws SQLException {
        JdbcIdAndVersionStream jdbcIdAndVersionStream = new JdbcIdAndVersionStream(connection, SQL, idAndVersionFactory);
        jdbcIdAndVersionStream.close();
        Mockito.verifyNoMoreInteractions(connection);
    }

    @Test
    public void shouldExecuteSQLQuery() throws SQLException {
        Mockito.when(connection.createStatement()).thenReturn(statement);
        //TODO: Handle scrolling properly
        Mockito.when(statement.executeQuery(SQL)).thenReturn(resultSet);
        Mockito.when(resultSet.getMetaData()).thenReturn(metaData);
        JdbcIdAndVersionStream jdbcIdAndVersionStream = new JdbcIdAndVersionStream(connection, SQL, idAndVersionFactory);
        jdbcIdAndVersionStream.open();
        IdAndVersionResultSetIterator iterator = (IdAndVersionResultSetIterator) jdbcIdAndVersionStream.iterator();
        assertThat(iterator.getResultSet(), Is.is(resultSet));
        Mockito.verify(statement).executeQuery(SQL);
    }

    @Test
    public void shouldTearDownStatement() throws SQLException {
        Mockito.when(connection.createStatement()).thenReturn(statement);
        Mockito.when(statement.executeQuery(SQL)).thenReturn(resultSet);
        Mockito.when(resultSet.getMetaData()).thenReturn(metaData);
        JdbcIdAndVersionStream jdbcIdAndVersionStream = new JdbcIdAndVersionStream(connection, SQL, idAndVersionFactory);
        jdbcIdAndVersionStream.open();
        jdbcIdAndVersionStream.iterator();
        jdbcIdAndVersionStream.close();
        Mockito.verify(resultSet).close();
        Mockito.verify(statement).close();
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionIfResultSetFailsToClose() throws SQLException {
        try {
            Mockito.when(connection.createStatement()).thenReturn(statement);
            Mockito.when(statement.executeQuery(SQL)).thenReturn(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception");
        }
        Mockito.doThrow(new SQLException()).when(resultSet).close();
        JdbcIdAndVersionStream jdbcIdAndVersionStream = new JdbcIdAndVersionStream(connection, SQL, idAndVersionFactory);
        jdbcIdAndVersionStream.open();
        jdbcIdAndVersionStream.iterator();
        jdbcIdAndVersionStream.close();
    }
}
