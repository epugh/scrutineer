package com.aconex.scrutineer2;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class PrintStreamOutputVersionStreamVerifierListenerTest {

    private final StringIdAndVersion idAndVersion = new StringIdAndVersion("1", 10);

    private PrintStream printStream;
    private ByteArrayOutputStream outputStream;

    @Before
    public void setup() {
        outputStream = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
    }

    @Test()
    public void testOnMissingInPrimaryStream() throws Exception {
        PrintStreamOutputVersionStreamVerifierListener streamVerifierListener = new PrintStreamOutputVersionStreamVerifierListener(printStream);
        streamVerifierListener.onMissingInPrimaryStream(idAndVersion);
        assertEquals("NOTINPRIMARY\t1\t10\n", outputStream.toString());
    }

    @Test
    public void testOnMissingInSecondaryStream() throws Exception {
        PrintStreamOutputVersionStreamVerifierListener streamVerifierListener = new PrintStreamOutputVersionStreamVerifierListener(printStream);
        streamVerifierListener.onMissingInSecondaryStream(idAndVersion);
        assertEquals("NOTINSECONDARY\t1\t10\n", outputStream.toString());
    }

    @Test
    public void testOnVersionMisMatch() throws Exception {
        PrintStreamOutputVersionStreamVerifierListener streamVerifierListener = new PrintStreamOutputVersionStreamVerifierListener(printStream);
        streamVerifierListener.onVersionMisMatch(idAndVersion, new StringIdAndVersion("1", 9));
        assertEquals("MISMATCH\t1\t10\tsecondaryVersion=9\n", outputStream.toString());
    }
}
