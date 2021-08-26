package com.aconex.scrutineer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.aconex.scrutineer.elasticsearch.ElasticSearchIdAndVersionStream;
import com.aconex.scrutineer.jdbc.JdbcIdAndVersionStream;
import com.aconex.scrutineer.runtime.IdAndVersionStreamConnectorFactory;
import com.google.common.base.Function;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class ScrutineerTest {

    @Mock
    private ScrutineerCommandLineOptionsExtension options;

    @Mock
    private ElasticSearchIdAndVersionStream secondaryIdAndVersionStream;

    @Mock
    private JdbcIdAndVersionStream primaryIdAndVersionStream;


    @Mock
    private IdAndVersionStreamVerifier idAndVersionStreamVerifier;

    @Mock
    private IdAndVersionStreamVerifierListener standardListener, coincidentListener;

    @Mock
    private IdAndVersionStreamConnector primaryIdAndVersionStreamConnector, secondaryIdAndVersionStreamConnector;

    @Mock
    private IdAndVersionStreamConnectorFactory idAndVersionStreamConnectorFactory;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test(expected=RuntimeException.class)
    public void shouldRethrowExceptionInExecute() {
      Scrutineer scrutineer = spy(new Scrutineer(options));
      doThrow(new Exception()).when(scrutineer).verify();

      scrutineer.verify();
    }

    @Test
    public void testVerify() {
        Scrutineer scrutineer = spy(new Scrutineer(options, idAndVersionStreamConnectorFactory));
        doReturn(Pair.of(primaryIdAndVersionStreamConnector, secondaryIdAndVersionStreamConnector)).when(idAndVersionStreamConnectorFactory).createStreamConnectors();

        doReturn(primaryIdAndVersionStream).when(primaryIdAndVersionStreamConnector).connect(any(IdAndVersionFactory.class));
        doReturn(secondaryIdAndVersionStream).when(secondaryIdAndVersionStreamConnector).connect(any(IdAndVersionFactory.class));

        doNothing().when(scrutineer).verify(eq(secondaryIdAndVersionStream), eq(primaryIdAndVersionStream), any(IdAndVersionStreamVerifier.class), any(IdAndVersionStreamVerifierListener.class));
        scrutineer.verify();

        verify(scrutineer).verify(eq(secondaryIdAndVersionStream), eq(primaryIdAndVersionStream), any(IdAndVersionStreamVerifier.class), any(IdAndVersionStreamVerifierListener.class));

    }

    @Test
    public void testShouldUseCoincidentFilteredStreamListenerIfOptionProvided() {
        doReturn(true).when(options).ignoreTimestampsDuringRun();
        Scrutineer scrutineer = spy(new Scrutineer(options));

        doReturn(coincidentListener).when(scrutineer).createCoincidentPrintStreamListener();
        doReturn(standardListener).when(scrutineer).createStandardPrintStreamListener();

        scrutineer.verify(secondaryIdAndVersionStream, primaryIdAndVersionStream, idAndVersionStreamVerifier, standardListener);

        verify(scrutineer).verify(secondaryIdAndVersionStream, primaryIdAndVersionStream, idAndVersionStreamVerifier, standardListener);
        verify(idAndVersionStreamVerifier).verify(any(IdAndVersionStream.class), any(IdAndVersionStream.class), eq(standardListener));
    }

    @Test
    public void testShouldUseStandardPrintStreamListenerIfOptionProvided() {
        Scrutineer scrutineer = spy(new Scrutineer(options));

        doReturn(coincidentListener).when(scrutineer).createCoincidentPrintStreamListener();
        doReturn(standardListener).when(scrutineer).createStandardPrintStreamListener();

        scrutineer.verify(secondaryIdAndVersionStream, primaryIdAndVersionStream, idAndVersionStreamVerifier, standardListener);

        verify(scrutineer).verify(secondaryIdAndVersionStream, primaryIdAndVersionStream, idAndVersionStreamVerifier, standardListener);
        verify(idAndVersionStreamVerifier).verify(any(IdAndVersionStream.class), any(IdAndVersionStream.class), eq(standardListener));
    }
}
