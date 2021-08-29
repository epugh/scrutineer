package com.aconex.scrutineer2;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.aconex.scrutineer2.javautil.JavaIteratorIdAndVersionStream;
import com.google.common.collect.Iterators;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class IdAndVersionStreamVerifierTest {

    private static final List<IdAndVersion> LIST = Collections.unmodifiableList(newArrayList(
            item("1", 1),
            item("2", 2),
            item("3", 3)));

    @Mock
    private IdAndVersionStreamConnector primaryStreamConnector;

    @Mock
    private IdAndVersionStreamConnector secondaryStreamConnector;

    @Mock
    private IdAndVersionStream primaryStream;

    @Mock
    private IdAndVersionStream secondaryStream;

    @Mock
    private IdAndVersionStreamVerifierListener idAndVersionStreamVerifierListener;
    private IdAndVersionStreamVerifier idAndVersionStreamVerifier;

    @Before
    public void setup() {
        initMocks(this);
        idAndVersionStreamVerifier = new IdAndVersionStreamVerifier();
        when(primaryStreamConnector.stream()).thenReturn(primaryStream);
        when(secondaryStreamConnector.stream()).thenReturn(secondaryStream);
        when(primaryStream.iterator()).thenReturn(LIST.iterator());
        when(secondaryStream.iterator()).thenReturn(LIST.iterator());
    }


    @Test
    public void shouldOpenBothStreams() {
        idAndVersionStreamVerifier.verify(primaryStreamConnector, secondaryStreamConnector, idAndVersionStreamVerifierListener);
        verify(primaryStreamConnector).open();
        verify(secondaryStreamConnector).open();
    }

    @Test
    public void shouldCloseBothStreams() throws IOException {
        idAndVersionStreamVerifier.verify(primaryStreamConnector, secondaryStreamConnector, idAndVersionStreamVerifierListener);
        verify(primaryStreamConnector).close();
        verify(secondaryStreamConnector).close();
    }

    @Test
    public void shouldCloseWhenOpenThrowsAnException() throws IOException {
        doThrow(new RuntimeException()).when(secondaryStreamConnector).open();
        try {
            idAndVersionStreamVerifier.verify(primaryStreamConnector, secondaryStreamConnector, idAndVersionStreamVerifierListener);
        } catch (RuntimeException e) {
            //Expected
        }
        verify(primaryStreamConnector).close();
        verify(secondaryStreamConnector).close();
    }

    @Test
    public void shouldCloseSecondaryWhenPrimaryCloseThrowsException() throws IOException {
        doThrow(new RuntimeException()).when(primaryStreamConnector).close();
        try {
            idAndVersionStreamVerifier.verify(primaryStreamConnector, secondaryStreamConnector, idAndVersionStreamVerifierListener);
        } catch (RuntimeException e) {
            //Expected
        }
        verify(secondaryStreamConnector).close();
    }

    @Test
    public void shouldNotReportErrorsIfStreamsAreEqual() {
        when(primaryStreamConnector.stream()).thenReturn(streamOf(item(1), item(2), item(3)));
        when(secondaryStreamConnector.stream()).thenReturn(streamOf(item(1), item(2), item(3)));

        idAndVersionStreamVerifier.verify(
                primaryStreamConnector,
                secondaryStreamConnector,
                idAndVersionStreamVerifierListener);
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldNotReportErrorsIfStreamsAreEmpty() {
        when(primaryStreamConnector.stream()).thenReturn(streamOf());
        when(secondaryStreamConnector.stream()).thenReturn(streamOf());

        idAndVersionStreamVerifier.verify(
                primaryStreamConnector,
                secondaryStreamConnector,
                idAndVersionStreamVerifierListener);
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldReportMissingValuesIfPrimaryStreamIsEmpty() {
        when(primaryStreamConnector.stream()).thenReturn(streamOf());
        when(secondaryStreamConnector.stream()).thenReturn(streamOf(item(1), item(2)));
        idAndVersionStreamVerifier.verify(
                primaryStreamConnector,
                secondaryStreamConnector,
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onMissingInPrimaryStream(eq(new StringIdAndVersion("1", 1)));
        verify(idAndVersionStreamVerifierListener).onMissingInPrimaryStream(eq(new StringIdAndVersion("2", 2)));
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldReportMissingValuesIfSecondaryStreamIsEmpty() {
        when(primaryStreamConnector.stream()).thenReturn(streamOf(item(1), item(2)));
        when(secondaryStreamConnector.stream()).thenReturn(streamOf());
        idAndVersionStreamVerifier.verify(
                primaryStreamConnector,
                secondaryStreamConnector,
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onMissingInSecondaryStream(eq(new StringIdAndVersion("1", 1)));
        verify(idAndVersionStreamVerifierListener).onMissingInSecondaryStream(eq(new StringIdAndVersion("2", 2)));
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldReportMissingItemsAtTheEndOfTheSecondaryStream() {
        when(primaryStreamConnector.stream()).thenReturn(streamOf(item(1), item(2), item(3), item(4)));
        when(secondaryStreamConnector.stream()).thenReturn(streamOf(item(1), item(2), item(3)));
        idAndVersionStreamVerifier.verify(
                primaryStreamConnector,
                secondaryStreamConnector,
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onMissingInSecondaryStream(eq(new StringIdAndVersion("4", 4)));
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldReportMissingItemsAtTheEndOfThePrimaryStream() {
        when(primaryStreamConnector.stream()).thenReturn(streamOf(item(1), item(2), item(3)));
        when(secondaryStreamConnector.stream()).thenReturn(streamOf(item(1), item(2), item(3), item(4)));
        idAndVersionStreamVerifier.verify(
                primaryStreamConnector,
                secondaryStreamConnector,
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onMissingInPrimaryStream(eq(new StringIdAndVersion("4", 4)));
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldReportMissingItemsAtTheStartOfTheSecondaryStream() {
        when(primaryStreamConnector.stream()).thenReturn(streamOf(item(1), item(2), item(3), item(4)));
        when(secondaryStreamConnector.stream()).thenReturn(streamOf(item(2), item(3), item(4)));
        idAndVersionStreamVerifier.verify(
                primaryStreamConnector,
                secondaryStreamConnector,
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onMissingInSecondaryStream(eq(new StringIdAndVersion("1", 1)));
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldReportMissingItemsAtTheStartOfThePrimaryStream() {
        when(primaryStreamConnector.stream()).thenReturn(streamOf(item(2), item(3), item(4)));
        when(secondaryStreamConnector.stream()).thenReturn(streamOf(item(1), item(2), item(3), item(4)));
        idAndVersionStreamVerifier.verify(
                primaryStreamConnector,
                secondaryStreamConnector,
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onMissingInPrimaryStream(eq(new StringIdAndVersion("1", 1)));
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }


    @Test
    public void shouldReportMissingItemsInTheMiddleOfTheSecondaryStream() {
        when(primaryStreamConnector.stream()).thenReturn(streamOf(item(1), item(2), item(3), item(4)));
        when(secondaryStreamConnector.stream()).thenReturn(streamOf(item(1), item(2), item(4)));
        idAndVersionStreamVerifier.verify(
                primaryStreamConnector,
                secondaryStreamConnector,
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onMissingInSecondaryStream(eq(new StringIdAndVersion("3", 3)));
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldReportMissingItemsInTheMiddleOfThePrimaryStream() {
        when(primaryStreamConnector.stream()).thenReturn(streamOf(item(1), item(3), item(4)));
        when(secondaryStreamConnector.stream()).thenReturn(streamOf(item(1), item(2), item(3), item(4)));
        idAndVersionStreamVerifier.verify(
                primaryStreamConnector,
                secondaryStreamConnector,
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onMissingInPrimaryStream(eq(new StringIdAndVersion("2", 2)));
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldReportVersionMisMatches() {
        when(primaryStreamConnector.stream()).thenReturn(streamOf(item(1), item("2", 2), item(3), item(4)));
        when(secondaryStreamConnector.stream()).thenReturn(streamOf(item(1), item("2", 5), item(3), item(4)));
        idAndVersionStreamVerifier.verify(
                primaryStreamConnector,
                secondaryStreamConnector,
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onVersionMisMatch(eq(new StringIdAndVersion("2", 2)), eq(new StringIdAndVersion("2", 5)));
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldReportMismatchedAtEndOfStream() {
        when(primaryStreamConnector.stream()).thenReturn(streamOf(item(1), item(2), item("3", 55)));
        when(secondaryStreamConnector.stream()).thenReturn(streamOf(item(1), item(2), item("3", 33)));
        idAndVersionStreamVerifier.verify(
                primaryStreamConnector,
                secondaryStreamConnector,
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onVersionMisMatch(eq(new StringIdAndVersion("3", 55)), eq(new StringIdAndVersion("3", 33)));
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldReportMismatchIfPreceededByMissingItem() {
        when(primaryStreamConnector.stream()).thenReturn(streamOf(item(1), item("2", 2), item(3), item(4)));
        when(secondaryStreamConnector.stream()).thenReturn(streamOf(item(1), item("3", 42), item(4)));
        idAndVersionStreamVerifier.verify(
                primaryStreamConnector,
                secondaryStreamConnector,
                idAndVersionStreamVerifierListener);

        verify(idAndVersionStreamVerifierListener).onMissingInSecondaryStream(eq(new StringIdAndVersion("2", 2)));
        verify(idAndVersionStreamVerifierListener).onVersionMisMatch(eq(new StringIdAndVersion("3", 3)), eq(new StringIdAndVersion("3", 42)));
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldNotifyOnPrimaryStreamProcessed() {
        when(primaryStreamConnector.stream()).thenReturn(streamOf(item(1), item(2)));
        when(secondaryStreamConnector.stream()).thenReturn(streamOf(item(1), item(2), item(3)));
        idAndVersionStreamVerifier.verify(
                primaryStreamConnector,
                secondaryStreamConnector,
                idAndVersionStreamVerifierListener);

        verify(idAndVersionStreamVerifierListener).onStreamComparison(item(1), item(1));
        verify(idAndVersionStreamVerifierListener).onStreamComparison(item(2), item(2));
        verify(idAndVersionStreamVerifierListener).onStreamComparison(null, item(3));
    }

    private void verifyOnCompletion(IdAndVersionStreamVerifierListener idAndVersionStreamVerifierListener) {
        verify(idAndVersionStreamVerifierListener).onVerificationCompleted();
    }

    private static JavaIteratorIdAndVersionStream streamOf(IdAndVersion... items) {
        return new JavaIteratorIdAndVersionStream(Iterators.forArray(items));
    }

    static IdAndVersion item(long version) {
        return new StringIdAndVersion("" + version, version);
    }

    static IdAndVersion item(String id, long version) {
        return new StringIdAndVersion(id, version);
    }
}
