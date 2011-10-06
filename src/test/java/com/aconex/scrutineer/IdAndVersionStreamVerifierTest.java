package com.aconex.scrutineer;

import com.aconex.scrutineer.javautil.JavaIteratorIdAndVersionStream;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

public class IdAndVersionStreamVerifierTest {

    private static final List<IdAndVersion> LIST = newArrayList(
            item("1", 1),
            item("3", 3),
            item("2", 2));

    @Mock
    private IdAndVersionStream primaryStream;

    @Mock
    private IdAndVersionStream secondayStream;

    @Mock
    private IdAndVersionStreamVerifierListener idAndVersionStreamVerifierListener;
    
    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldOpenBothStreams() {
        IdAndVersionStreamVerifier idAndVersionStreamVerifier = new IdAndVersionStreamVerifier();
        idAndVersionStreamVerifier.verify(primaryStream, secondayStream, idAndVersionStreamVerifierListener);
        verify(primaryStream).open();
        verify(secondayStream).open();
    }

    @Test
    public void shouldCloseBothStreams() {
        IdAndVersionStreamVerifier idAndVersionStreamVerifier = new IdAndVersionStreamVerifier();
        idAndVersionStreamVerifier.verify(primaryStream, secondayStream, idAndVersionStreamVerifierListener);
        verify(primaryStream).close();
        verify(secondayStream).close();
    }

    @Test
    public void shouldNotReportErrorsIfStreamsAreEqual() {
        IdAndVersionStreamVerifier idAndVersionStreamVerifier = new IdAndVersionStreamVerifier();
        idAndVersionStreamVerifier.verify(
                new JavaIteratorIdAndVersionStream(LIST.iterator()),
                new JavaIteratorIdAndVersionStream(LIST.iterator()),
                idAndVersionStreamVerifierListener);
        verifyZeroInteractions(idAndVersionStreamVerifierListener);
    }

    static IdAndVersion item(String id, long version) {
        return new IdAndVersion(id, version);
    }
}
