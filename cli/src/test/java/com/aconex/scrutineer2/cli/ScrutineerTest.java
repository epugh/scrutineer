package com.aconex.scrutineer2.cli;

import com.aconex.scrutineer2.CoincidentFilteredStreamVerifierListener;
import com.aconex.scrutineer2.IdAndVersionStreamConnector;
import com.aconex.scrutineer2.IdAndVersionStreamVerifier;
import com.aconex.scrutineer2.IdAndVersionStreamVerifierListener;
import com.aconex.scrutineer2.PrintStreamOutputVersionStreamVerifierListener;
import com.aconex.scrutineer2.cli.Scrutineer;
import com.aconex.scrutineer2.cli.config.CliConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;


public class ScrutineerTest {

    @Mock
    private IdAndVersionStreamVerifier idAndVersionStreamVerifier;

    @Mock
    private IdAndVersionStreamConnector primaryIdAndVersionStreamConnector, secondaryIdAndVersionStreamConnector;

    @Mock
    private CliConfig cliConfig;

    private Scrutineer scrutineer;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        scrutineer = new Scrutineer(cliConfig, idAndVersionStreamVerifier);
    }


    @Test(expected = RuntimeException.class)
    public void shouldRethrowExceptionInExecute() {
        Scrutineer scrutineerMock = spy(scrutineer);
        doThrow(new Exception()).when(scrutineerMock).verify();
        scrutineerMock.verify();
    }

    @Test
    public void testShouldUseCoincidentFilteredStreamListenerIfOptionProvided() {
        doReturn(true).when(cliConfig).ignoreTimestampsDuringRun();
        Scrutineer scrutineer = new Scrutineer(cliConfig, idAndVersionStreamVerifier);
        IdAndVersionStreamVerifierListener verifierListener = scrutineer.createVerifierListener();
        assertTrue(verifierListener instanceof CoincidentFilteredStreamVerifierListener);
    }

    @Test
    public void testShouldUseStandardPrintStreamListenerIfOptionProvided() {
        Scrutineer scrutineer = new Scrutineer(cliConfig, idAndVersionStreamVerifier);
        IdAndVersionStreamVerifierListener verifierListener = scrutineer.createVerifierListener();
        assertTrue(verifierListener instanceof PrintStreamOutputVersionStreamVerifierListener);
    }
}
