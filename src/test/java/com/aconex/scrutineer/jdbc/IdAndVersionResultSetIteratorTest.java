package com.aconex.scrutineer.jdbc;

import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.aconex.scrutineer.HasIdAndVersionMatcher.hasIdAndVersion;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class IdAndVersionResultSetIteratorTest {

    private static final String ID = "ID";
    private static final long VERSION = 123L;
    @org.mockito.Mock
    private ResultSet resultSet;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldReturnNextIfResultSetHasMoreResults() throws SQLException {
        when(resultSet.next()).thenReturn(true);
        IdAndVersionResultSetIterator idAndVersionResultSetIterator = new IdAndVersionResultSetIterator(resultSet);
        assertThat(idAndVersionResultSetIterator.hasNext(), is(true));
    }

    @Test
    public void shouldReturnFalseIfResultSetHasNoMoreResults() throws SQLException {
        when(resultSet.next()).thenReturn(false);
        IdAndVersionResultSetIterator idAndVersionResultSetIterator = new IdAndVersionResultSetIterator(resultSet);
        assertThat(idAndVersionResultSetIterator.hasNext(), is(false));
    }

    @Test
    public void shouldGetTheNextIdAndVersion() throws SQLException {
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString(1)).thenReturn(ID);
        when(resultSet.getLong(2)).thenReturn(VERSION);
        IdAndVersionResultSetIterator idAndVersionResultSetIterator = new IdAndVersionResultSetIterator(resultSet);
        assertThat(idAndVersionResultSetIterator.next(), hasIdAndVersion(ID,VERSION));
        assertThat(idAndVersionResultSetIterator.hasNext(), is(false));
    }
}
