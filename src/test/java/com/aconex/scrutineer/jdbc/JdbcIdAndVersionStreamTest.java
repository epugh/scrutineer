package com.aconex.scrutineer.jdbc;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class JdbcIdAndVersionStreamTest {

    @Mock
    private DataSource dataSource;
    @Mock
    private Connection connection;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldOpenNewJdbcConnection() throws SQLException {
        JdbcIdAndVersionStream jdbcIdAndVersionStream = new JdbcIdAndVersionStream(dataSource);
        jdbcIdAndVersionStream.open();
        verify(dataSource).getConnection();
    }

    @Test
    public void shouldCloseConnection() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        JdbcIdAndVersionStream jdbcIdAndVersionStream = new JdbcIdAndVersionStream(dataSource);
        jdbcIdAndVersionStream.open();
        jdbcIdAndVersionStream.close();
        verify(connection).close();
    }

    @Test
    public void closeShouldDoNothingIfNotOpen() throws SQLException {
        JdbcIdAndVersionStream jdbcIdAndVersionStream = new JdbcIdAndVersionStream(dataSource);
        jdbcIdAndVersionStream.close();
        verifyNoMoreInteractions(dataSource);
    }
}
