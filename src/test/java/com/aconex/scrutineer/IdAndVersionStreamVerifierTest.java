package com.aconex.scrutineer;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Collections;
import java.util.List;

import com.aconex.scrutineer.javautil.JavaIteratorIdAndVersionStream;
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
    public void shouldCloseWhenOpenThrowsAnException() {
        doThrow(new RuntimeException()).when(secondayStream).open();
        try {
            idAndVersionStreamVerifier.verify(primaryStream, secondayStream, idAndVersionStreamVerifierListener);
        } catch (RuntimeException e) {
            //Expected
        }
        verify(primaryStream).close();
        verify(secondayStream).close();
    }

    @Test
    public void shouldCloseSecondaryWhenPrimaryCloseThrowsException() {
        doThrow(new RuntimeException()).when(primaryStream).close();
        try {
            idAndVersionStreamVerifier.verify(primaryStream, secondayStream, idAndVersionStreamVerifierListener);
        } catch (RuntimeException e) {
            //Expected
        }
        verify(secondayStream).close();
    }


    @Test
    public void shouldNotReportErrorsIfStreamsAreEqual() {
        idAndVersionStreamVerifier.verify(
                streamOf(item(1), item(2), item(3)),
                streamOf(item(1), item(2), item(3)),
                idAndVersionStreamVerifierListener);
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldNotReportErrorsIfStreamsAreEmpty() {
        idAndVersionStreamVerifier.verify(
                streamOf(),
                streamOf(),
                idAndVersionStreamVerifierListener);
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldReportMissingValuesIfPrimaryStreamIsEmpty() {
        idAndVersionStreamVerifier.verify(
                streamOf(),
                streamOf(item(1), item(2)),
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onMissingInPrimaryStream(eq(new StringIdAndVersion("1", 1)));
        verify(idAndVersionStreamVerifierListener).onMissingInPrimaryStream(eq(new StringIdAndVersion("2", 2)));
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldReportMissingValuesIfSecondaryStreamIsEmpty() {
        idAndVersionStreamVerifier.verify(
                streamOf(item(1), item(2)),
                streamOf(),
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onMissingInSecondaryStream(eq(new StringIdAndVersion("1", 1)));
        verify(idAndVersionStreamVerifierListener).onMissingInSecondaryStream(eq(new StringIdAndVersion("2", 2)));
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldReportMissingItemsAtTheEndOfTheSecondaryStream() {
        idAndVersionStreamVerifier.verify(
                streamOf(item(1), item(2), item(3), item(4)),
                streamOf(item(1), item(2), item(3)),
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onMissingInSecondaryStream(eq(new StringIdAndVersion("4", 4)));
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldReportMissingItemsAtTheEndOfThePrimaryStream() {
        idAndVersionStreamVerifier.verify(
                streamOf(item(1), item(2), item(3)),
                streamOf(item(1), item(2), item(3), item(4)),
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onMissingInPrimaryStream(eq(new StringIdAndVersion("4", 4)));
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldReportMissingItemsAtTheStartOfTheSecondaryStream() {
        idAndVersionStreamVerifier.verify(
                streamOf(item(1), item(2), item(3), item(4)),
                streamOf(item(2), item(3), item(4)),
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onMissingInSecondaryStream(eq(new StringIdAndVersion("1", 1)));
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldReportMissingItemsAtTheStartOfThePrimaryStream() {
        idAndVersionStreamVerifier.verify(
                streamOf(item(2), item(3), item(4)),
                streamOf(item(1), item(2), item(3), item(4)),
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onMissingInPrimaryStream(eq(new StringIdAndVersion("1", 1)));
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }


    @Test
    public void shouldReportMissingItemsInTheMiddleOfTheSecondaryStream() {
        idAndVersionStreamVerifier.verify(
                streamOf(item(1), item(2), item(3), item(4)),
                streamOf(item(1), item(2), item(4)),
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onMissingInSecondaryStream(eq(new StringIdAndVersion("3", 3)));
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldReportMissingItemsInTheMiddleOfThePrimaryStream() {
        idAndVersionStreamVerifier.verify(
                streamOf(item(1), item(3), item(4)),
                streamOf(item(1), item(2), item(3), item(4)),
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onMissingInPrimaryStream(eq(new StringIdAndVersion("2", 2)));
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldReportVersionMisMatches() {
        idAndVersionStreamVerifier.verify(
                streamOf(item(1), item("2", 2), item(3), item(4)),
                streamOf(item(1), item("2", 5), item(3), item(4)),
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onVersionMisMatch(eq(new StringIdAndVersion("2", 2)), eq(new StringIdAndVersion("2", 5)));
        verifyOnCompletion(idAndVersionStreamVerifierListener);
    }

    @Test
    public void shouldReportMismatchedAtEndOfStream() {
        idAndVersionStreamVerifier.verify(
                streamOf(item(1), item(2), item("3", 55)),
                streamOf(item(1), item(2), item("3", 33)),
                idAndVersionStreamVerifierListener);
        verify(idAndVersionStreamVerifierListener).onVersionMisMatch(eq(new StringIdAndVersion("3", 55)), eq(new StringIdAndVersion("3", 33)));
        verifyOnCompletion(idAndVersionStreamVerifierListener);

    }

    @Test
    public void shouldReportMismatchIfPreceededByMissingItem() {
        idAndVersionStreamVerifier.verify(
                streamOf(item(1), item("2", 2), item(3), item(4)),
                streamOf(item(1), item("3", 42), item(4)),
                idAndVersionStreamVerifierListener);

        verify(idAndVersionStreamVerifierListener).onMissingInSecondaryStream(eq(new StringIdAndVersion("2", 2)));
        verify(idAndVersionStreamVerifierListener).onVersionMisMatch(eq(new StringIdAndVersion("3", 3)), eq(new StringIdAndVersion("3", 42)));
        verifyOnCompletion(idAndVersionStreamVerifierListener);

    }

    @Test
    public void shouldNotifyOnPrimaryStreamProcessed() {
        idAndVersionStreamVerifier.verify(
                streamOf(item(1), item(2)),
                streamOf(item(1), item(2), item(3)),
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
