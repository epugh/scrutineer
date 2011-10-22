package com.aconex.scrutineer;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class StdOutIdAndVersionStreamVerifierListenerTest {

    private final IdAndVersion idAndVersion = new IdAndVersion("1", 10);

    @Mock
    private PrintStream printStream;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOnMissingInPrimaryStream() throws Exception {
        StdOutIdAndVersionStreamVerifierListener streamVerifierListener = new StdOutIdAndVersionStreamVerifierListener(printStream);
        streamVerifierListener.onMissingInPrimaryStream(idAndVersion);
        verify(printStream).println("1");
        verifyNoMoreInteractions(printStream);
    }

    @Test
    public void testOnMissingInSecondaryStream() throws Exception {
        StdOutIdAndVersionStreamVerifierListener streamVerifierListener = new StdOutIdAndVersionStreamVerifierListener(printStream);
        streamVerifierListener.onMissingInSecondaryStream(idAndVersion);
        verify(printStream).println("1");
        verifyNoMoreInteractions(printStream);
    }


    @Test
    public void testOnVersionMisMatch() throws Exception {

    }
}
