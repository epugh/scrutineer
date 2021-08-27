package com.aconex.scrutineer2.elasticsearch;

import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.ObjectOutputStream;

import com.aconex.scrutineer2.IdAndVersion;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class IdAndVersionDataWriterTest {

    @Mock
    private ObjectOutputStream objectOutputStream;

    @Mock
    private IdAndVersion idAndVersion;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldWriteEntry() throws IOException {
        IdAndVersionDataWriter idAndVersionDataWriter = new IdAndVersionDataWriter(objectOutputStream);
        idAndVersionDataWriter.writeEntry(idAndVersion);

        verify(idAndVersion).writeToStream(objectOutputStream);
    }

    @Test
    public void shouldCloseStream() throws IOException {
        IdAndVersionDataWriter idAndVersionDataWriter = new IdAndVersionDataWriter(objectOutputStream);
        idAndVersionDataWriter.close();

        verify(objectOutputStream).close();
    }

}
