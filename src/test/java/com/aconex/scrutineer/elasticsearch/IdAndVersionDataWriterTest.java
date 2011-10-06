package com.aconex.scrutineer.elasticsearch;

import com.aconex.scrutineer.IdAndVersion;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.ObjectOutputStream;

import static org.mockito.Mockito.verify;

public class IdAndVersionDataWriterTest {

    private static final long ID = 12;
    private static final long VERSION = 77;
    @Mock
    private ObjectOutputStream objectOutputStream;

    @Before public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test public void shouldWriteEntry() throws IOException {
        IdAndVersionDataWriter idAndVersionDataWriter = new IdAndVersionDataWriter(objectOutputStream);
        idAndVersionDataWriter.writeEntry(new IdAndVersion(ID,VERSION));

        verify(objectOutputStream).writeLong(ID);
        verify(objectOutputStream).writeLong(VERSION);
    }

    @Test public void shouldCloseStream() throws IOException {
        IdAndVersionDataWriter idAndVersionDataWriter = new IdAndVersionDataWriter(objectOutputStream);
        idAndVersionDataWriter.close();

        verify(objectOutputStream).close();
    }

}
