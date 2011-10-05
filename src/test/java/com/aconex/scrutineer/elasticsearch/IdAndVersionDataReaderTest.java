package com.aconex.scrutineer.elasticsearch;

import com.aconex.scrutineer.IdAndVersion;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class IdAndVersionDataReaderTest {

    private static final long ID = 77;
    private static final long VERSION = 12;
    @Mock
    private ObjectInputStream objectInputStream;
    private IdAndVersionDataReader idAndVersionDataReader;


    @Before
    public void setup() {
        initMocks(this);
        idAndVersionDataReader = new IdAndVersionDataReader(objectInputStream);
    }

    @Test public void shouldGiveAndEstimateOfSize() {
        assertThat(idAndVersionDataReader.estimateSizeInBytes(new IdAndVersion(ID,VERSION)),is(16));
    }

    @Test public void shouldReadNextIdAndVersionObjectFromStream() throws IOException {
        when(objectInputStream.readLong()).thenReturn(ID).thenReturn(VERSION);
        IdAndVersion idAndVersion = idAndVersionDataReader.readNext();
        assertThat(idAndVersion.getId(), is(ID));
        assertThat(idAndVersion.getVersion(), is(VERSION));
    }

    @Test public void shouldReturnNullOnEndOfStream() throws IOException {
        when(objectInputStream.readLong()).thenThrow(new EOFException());
        assertThat(idAndVersionDataReader.readNext(), is(nullValue()));
    }
}
