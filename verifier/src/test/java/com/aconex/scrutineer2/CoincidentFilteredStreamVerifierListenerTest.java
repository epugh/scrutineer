package com.aconex.scrutineer2;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aconex.scrutineer2.javautil.ControlledTimeSource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CoincidentFilteredStreamVerifierListenerTest {


    @Mock
    private IdAndVersionStreamVerifierListener otherListener;

    @Mock
    private IdAndVersion idAndVersion, primaryIdAndVersion, secondaryIdAndVersion;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testShouldDelegateOnMissingInPrimaryStream() {
        CoincidentFilteredStreamVerifierListener coincidentFilteredStreamVerifierListener = new CoincidentFilteredStreamVerifierListener(otherListener);

        coincidentFilteredStreamVerifierListener.onMissingInPrimaryStream(idAndVersion);

        verify(otherListener).onMissingInPrimaryStream(idAndVersion);
        verify(otherListener, never()).onMissingInSecondaryStream(idAndVersion);
    }

    @Test
    public void testShouldDelegateOnMissingInSecondaryStream() {
        CoincidentFilteredStreamVerifierListener coincidentFilteredStreamVerifierListener = new CoincidentFilteredStreamVerifierListener(otherListener);

        coincidentFilteredStreamVerifierListener.onMissingInSecondaryStream(idAndVersion);

        verify(otherListener).onMissingInSecondaryStream(idAndVersion);
        verify(otherListener, never()).onMissingInPrimaryStream(idAndVersion);
    }

    @Test
    public void shouldDelegateOnMismatchIfPrimaryTimestampIsBeforeStartOfRun() {
        CoincidentFilteredStreamVerifierListener coincidentFilteredStreamVerifierListener = new CoincidentFilteredStreamVerifierListener(new ControlledTimeSource(30), otherListener);


        when(primaryIdAndVersion.getVersion()).thenReturn(10L);
        when(secondaryIdAndVersion.getVersion()).thenReturn(20L);

        coincidentFilteredStreamVerifierListener.onVersionMisMatch(primaryIdAndVersion, secondaryIdAndVersion);

        verify(otherListener).onVersionMisMatch(primaryIdAndVersion, secondaryIdAndVersion);
    }

    @Test
    public void shouldNotDelegateOnMismatchIfPrimaryTimestampIsAfterRunStarted() {
        CoincidentFilteredStreamVerifierListener coincidentFilteredStreamVerifierListener = new CoincidentFilteredStreamVerifierListener(new ControlledTimeSource(5), otherListener);

        when(primaryIdAndVersion.getVersion()).thenReturn(10L);
        when(secondaryIdAndVersion.getVersion()).thenReturn(20L);

        coincidentFilteredStreamVerifierListener.onVersionMisMatch(primaryIdAndVersion, secondaryIdAndVersion);

        verify(otherListener, never()).onVersionMisMatch(primaryIdAndVersion, secondaryIdAndVersion);
    }

    @Test
    public void shouldNotDelegateOnMismatchIfSecondaryTimestampIsAfterRunStarted() {
        CoincidentFilteredStreamVerifierListener coincidentFilteredStreamVerifierListener = new CoincidentFilteredStreamVerifierListener(new ControlledTimeSource(5), otherListener);

        when(primaryIdAndVersion.getVersion()).thenReturn(1L);
        when(secondaryIdAndVersion.getVersion()).thenReturn(20L);

        coincidentFilteredStreamVerifierListener.onVersionMisMatch(primaryIdAndVersion, secondaryIdAndVersion);

        verify(otherListener, never()).onVersionMisMatch(primaryIdAndVersion, secondaryIdAndVersion);
    }

    @Test
    public void shouldNotDelegateOnMismatchIfBothItemsTimestampsAreAfterRunStarted() {
        CoincidentFilteredStreamVerifierListener coincidentFilteredStreamVerifierListener = new CoincidentFilteredStreamVerifierListener(new ControlledTimeSource(5), otherListener);

        when(primaryIdAndVersion.getVersion()).thenReturn(10L);
        when(secondaryIdAndVersion.getVersion()).thenReturn(20L);

        coincidentFilteredStreamVerifierListener.onVersionMisMatch(primaryIdAndVersion, secondaryIdAndVersion);

        verify(otherListener, never()).onVersionMisMatch(primaryIdAndVersion, secondaryIdAndVersion);

    }

    @Test
    public void shouldNotDelegateOnMissingInSecondaryIfItemIsAfterRunStarted() {
        CoincidentFilteredStreamVerifierListener coincidentFilteredStreamVerifierListener = new CoincidentFilteredStreamVerifierListener(new ControlledTimeSource(5), otherListener);

        when(primaryIdAndVersion.getVersion()).thenReturn(10L);

        coincidentFilteredStreamVerifierListener.onMissingInSecondaryStream(primaryIdAndVersion);

        verify(otherListener, never()).onMissingInSecondaryStream(primaryIdAndVersion);

    }

    @Test
    public void shouldNotDelegateOnMissingInPrimaryIfItemIsAfterRunStarted() {
        CoincidentFilteredStreamVerifierListener coincidentFilteredStreamVerifierListener = new CoincidentFilteredStreamVerifierListener(new ControlledTimeSource(5), otherListener);

        when(secondaryIdAndVersion.getVersion()).thenReturn(20L);

        coincidentFilteredStreamVerifierListener.onMissingInPrimaryStream(secondaryIdAndVersion);

        verify(otherListener, never()).onMissingInPrimaryStream(secondaryIdAndVersion);

    }

    @Test
    public void shouldNotDelegateWhenTimeMoves(){

        ControlledTimeSource timeSource = new ControlledTimeSource(5);
        CoincidentFilteredStreamVerifierListener coincidentFilteredStreamVerifierListener = new CoincidentFilteredStreamVerifierListener(timeSource, otherListener);

        timeSource.setCurrentTime(25);

        when(secondaryIdAndVersion.getVersion()).thenReturn(20L);

        coincidentFilteredStreamVerifierListener.onMissingInPrimaryStream(secondaryIdAndVersion);

        verify(otherListener, never()).onMissingInPrimaryStream(secondaryIdAndVersion);

    }


}
