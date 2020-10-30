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
import com.google.common.base.Function;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ScrutineerTest {

    @Mock
    private ScrutineerCommandLineOptions options;

    @Mock
    private ElasticSearchIdAndVersionStream elasticSearchIdAndVersionStream;

    @Mock
    private JdbcIdAndVersionStream jdbcIdAndVersionStream;


    @Mock
    private IdAndVersionStreamVerifier idAndVersionStreamVerifier;

    @Mock
    private IdAndVersionStreamVerifierListener standardListener, coincidentListener;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecute() {
        Scrutineer scrutineer = spy(new Scrutineer(options));
        doNothing().when(scrutineer).verify();

        Scrutineer.execute(scrutineer);
        verify(scrutineer).verify();
        verify(scrutineer).close();
    }

    @Test(expected=RuntimeException.class)
    public void shouldRethrowExceptionInExecute() {
      Scrutineer scrutineer = spy(new Scrutineer(options));
      doThrow(new Exception()).when(scrutineer).verify();

      Scrutineer.execute(scrutineer);
    }

    @Test
    public void testVerify() {
        Scrutineer scrutineer = spy(new Scrutineer(options));
        doReturn(elasticSearchIdAndVersionStream).when(scrutineer).createElasticSearchIdAndVersionStream(eq(options));
        doReturn(jdbcIdAndVersionStream).when(scrutineer).createJdbcIdAndVersionStream(options);
        doNothing().when(scrutineer).verify(eq(elasticSearchIdAndVersionStream), eq(jdbcIdAndVersionStream), any(IdAndVersionStreamVerifier.class), any(IdAndVersionStreamVerifierListener.class));
        scrutineer.verify();

        verify(scrutineer).verify(eq(elasticSearchIdAndVersionStream), eq(jdbcIdAndVersionStream), any(IdAndVersionStreamVerifier.class), any(IdAndVersionStreamVerifierListener.class));

    }

    @Test
    public void testShouldUseCoincidentFilteredStreamListenerIfOptionProvided() {
        options.ignoreTimestampsDuringRun = true;
        Scrutineer scrutineer = spy(new Scrutineer(options));

        doReturn(coincidentListener).when(scrutineer).createCoincidentPrintStreamListener(any(Function.class));
        doReturn(standardListener).when(scrutineer).createStandardPrintStreamListener(any(Function.class));

        scrutineer.verify(elasticSearchIdAndVersionStream, jdbcIdAndVersionStream, idAndVersionStreamVerifier, standardListener);

        verify(scrutineer).verify(elasticSearchIdAndVersionStream, jdbcIdAndVersionStream, idAndVersionStreamVerifier, standardListener);
        verify(idAndVersionStreamVerifier).verify(any(IdAndVersionStream.class), any(IdAndVersionStream.class), eq(standardListener));
    }

    @Test
    public void testShouldUseStandardPrintStreamListenerIfOptionProvided() {
        Scrutineer scrutineer = spy(new Scrutineer(options));

        doReturn(coincidentListener).when(scrutineer).createCoincidentPrintStreamListener(any(Function.class));
        doReturn(standardListener).when(scrutineer).createStandardPrintStreamListener(any(Function.class));

        scrutineer.verify(elasticSearchIdAndVersionStream, jdbcIdAndVersionStream, idAndVersionStreamVerifier, standardListener);

        verify(scrutineer).verify(elasticSearchIdAndVersionStream, jdbcIdAndVersionStream, idAndVersionStreamVerifier, standardListener);
        verify(idAndVersionStreamVerifier).verify(any(IdAndVersionStream.class), any(IdAndVersionStream.class), eq(standardListener));
    }

    @Test
    public void testClose() throws Exception {

        Scrutineer scrutineer = spy(new Scrutineer(options));
        doNothing().when(scrutineer).closeJdbcConnection();
        doNothing().when(scrutineer).closeElasticSearchConnections();

        scrutineer.close();

        verify(scrutineer).closeJdbcConnection();
        verify(scrutineer).closeElasticSearchConnections();
    }
}
