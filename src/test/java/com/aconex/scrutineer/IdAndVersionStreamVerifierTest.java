package com.aconex.scrutineer;

import com.aconex.scrutineer.javautil.JavaIteratorIdAndVersionStream;
import com.google.common.collect.Iterators;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

import static com.aconex.scrutineer.HasIdAndVersionMatcher.hasIdAndVersion;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class IdAndVersionStreamVerifierTest {

    private static final List<IdAndVersion> LIST = Collections.unmodifiableList(newArrayList(
            item("1", 1),
            item("3", 3),
            item("2", 2)));

    @Mock
    private IdAndVersionStream primaryStream;

    @Mock
    private IdAndVersionStream secondayStream;

    @Mock
    private IdAndVersionStreamVerifierListener idAndVersionStreamVerifierListener;
    private IdAndVersionStreamVerifier idAndVersionStreamVerifier;

    @Before
    public void setup() {
        initMocks(this);
        idAndVersionStreamVerifier = new IdAndVersionStreamVerifier();
        when(primaryStream.iterator()).thenReturn(LIST.iterator());
        when(secondayStream.iterator()).thenReturn(LIST.iterator());
    }

    @Test
    public void shouldOpenBothStreams() {
        idAndVersionStreamVerifier.verify(primaryStream, secondayStream, idAndVersionStreamVerifierListener);
        verify(primaryStream).open();
        verify(secondayStream).open();
    }

    @Test
    public void shouldCloseBothStreams() {
        idAndVersionStreamVerifier.verify(primaryStream, secondayStream, idAndVersionStreamVerifierListener);
        verify(primaryStream).close();
        verify(secondayStream).close();
    }

    @Test
    public void shouldNotReportErrorsIfStreamsAreEqual() {
        idAndVersionStreamVerifier.verify(
                streamOf(item(1), item(2), item(3)),
                streamOf(item(1), item(2), item(3)),
                idAndVersionStreamVerifierListener);
        verifyZeroInteractions(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldReportMissingItemsAtTheEndOfTheSecondaryStream() {
        idAndVersionStreamVerifier.verify(
                streamOf(item(1), item(2), item(3), item(4)),
                streamOf(item(1), item(2), item(3)),
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onMissingInSecondaryStream(argThat(hasIdAndVersion("4",4)));
    }

    @Test
    public void shouldReportMissingItemsAtTheEndOfThePrimaryStream() {
        idAndVersionStreamVerifier.verify(
                streamOf(item(1), item(2), item(3)),
                streamOf(item(1), item(2), item(3), item(4)),
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onMissingInPrimaryStream(argThat(hasIdAndVersion("4",4)));
    }

    @Test
    public void shouldReportMissingItemsAtTheStartOfTheSecondaryStream() {
        idAndVersionStreamVerifier.verify(
                streamOf(item(1), item(2), item(3), item(4)),
                streamOf(item(2), item(3), item(4)),
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onMissingInSecondaryStream(argThat(hasIdAndVersion("1",1)));
    }

    @Test
    public void shouldReportMissingItemsAtTheStartOfThePrimaryStream() {
        idAndVersionStreamVerifier.verify(
                streamOf(item(2), item(3), item(4)),
                streamOf(item(1), item(2), item(3), item(4)),
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onMissingInPrimaryStream(argThat(hasIdAndVersion("1",1)));
    }

    @Test
    public void shouldReportMissingItemsInTheMiddleOfTheSecondaryStream() {
        idAndVersionStreamVerifier.verify(
                streamOf(item(1), item(2), item(3), item(4)),
                streamOf(item(1), item(2),          item(4)),
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onMissingInSecondaryStream(argThat(hasIdAndVersion("3",3)));
    }

    @Test
    public void shouldReportMissingItemsInTheMiddleOfThePrimaryStream() {
        idAndVersionStreamVerifier.verify(
                streamOf(item(1),          item(3), item(4)),
                streamOf(item(1), item(2), item(3), item(4)),
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onMissingInPrimaryStream(argThat(hasIdAndVersion("2",2)));
    }

    @Test
    public void shouldReportVersionMisMatches() {
        idAndVersionStreamVerifier.verify(
                streamOf(item(1), item("2",2), item(3), item(4)),
                streamOf(item(1), item("2",5), item(3), item(4)),
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onVersionMisMatch(argThat(hasIdAndVersion("2",2)), argThat(hasIdAndVersion("2",5)));
    }

    private static JavaIteratorIdAndVersionStream streamOf(IdAndVersion ... items) {
        return new JavaIteratorIdAndVersionStream(Iterators.forArray(items));
    }

    static IdAndVersion item(long version) {
        return new IdAndVersion(""+version, version);
    }

    static IdAndVersion item(String id, long version) {
        return new IdAndVersion(id, version);
    }
}
