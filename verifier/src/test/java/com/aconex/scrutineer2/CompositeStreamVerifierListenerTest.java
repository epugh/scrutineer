package com.aconex.scrutineer2;

import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CompositeStreamVerifierListenerTest {

    @Mock
    private IdAndVersionStreamVerifierListener otherListener1;
    @Mock
    private IdAndVersionStreamVerifierListener otherListener2;

    @Mock
    private IdAndVersion idAndVersion, primaryIdAndVersion, secondaryIdAndVersion;

    private CompositeStreamVerifierListener testInstance;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.testInstance = new CompositeStreamVerifierListener(Arrays.asList(otherListener1, otherListener2));
    }

    @Test
    public void shouldDelegateToAllListenersOnPrimaryStreamProcessed() {

        testInstance.onStreamComparison(primaryIdAndVersion, secondaryIdAndVersion);

        verify(otherListener1).onStreamComparison(primaryIdAndVersion, secondaryIdAndVersion);
        verify(otherListener2).onStreamComparison(primaryIdAndVersion, secondaryIdAndVersion);
    }

    @Test
    public void shouldDelegateToAllListenersOnMissingInPrimaryStream() {
        testInstance.onMissingInPrimaryStream(idAndVersion);

        verify(otherListener1).onMissingInPrimaryStream(idAndVersion);
        verify(otherListener2).onMissingInPrimaryStream(idAndVersion);
    }

    @Test
    public void shouldDelegateToAllListenersOnVersionMisMatch() {
        testInstance.onVersionMisMatch(primaryIdAndVersion, secondaryIdAndVersion);

        verify(otherListener1).onVersionMisMatch(primaryIdAndVersion, secondaryIdAndVersion);
        verify(otherListener2).onVersionMisMatch(primaryIdAndVersion, secondaryIdAndVersion);
    }

    @Test
    public void shouldDelegateToAllListenersOnVerificationStarted() {
        testInstance.onVerificationStarted();

        verify(otherListener1).onVerificationStarted();
        verify(otherListener2).onVerificationStarted();
    }

    @Test
    public void shouldDelegateToAllListenersOnVerificationCompleted() {
        testInstance.onVerificationCompleted();

        verify(otherListener1).onVerificationCompleted();
        verify(otherListener2).onVerificationCompleted();
    }
}
