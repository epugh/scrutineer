package com.aconex.scrutineer;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LogUtilsTest {

    private static final String LOG_MESSAGE = "Log Message";

    @Mock
    private Logger logger;

    public LogUtilsTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldSetLoggerAsAStatigField() {
        assertThat(StaticTester.class.getName(), is(StaticTester.LOG.getName()));
    }

    @Test(expected = Exception.class )
    public void shouldThrowExceptionForNonStaticLogField() {
        new NonStaticTester();
    }

    @Test(expected = Exception.class )
    public void shouldThrowExceptionIfLogIsInstantiatedInline() {
        LogUtils.loggerForThisClass();
    }

    @Test
    public void shouldNotLogWhenDebugIsDisabled() {
        reset(logger);
        when(logger.isDebugEnabled()).thenReturn(false);
        LogUtils.debug(logger, LOG_MESSAGE);
        verify(logger, times(0)).debug(LOG_MESSAGE);
    }

    @Test
    public void shouldLogWhenDebugIsEnabled() {
        when(logger.isDebugEnabled()).thenReturn(true);
        LogUtils.debug(logger, LOG_MESSAGE);
        verify(logger).debug(LOG_MESSAGE);
    }

    @Test
    public void shouldLogInfoMessages() {
        LogUtils.info(logger, LOG_MESSAGE);
        verify(logger).info(LOG_MESSAGE);
    }

    @Test
    public void shouldLogErrorMessages() {
        LogUtils.error(logger, LOG_MESSAGE);
        verify(logger).error(LOG_MESSAGE);
    }


    // CHECKSTYLE:OFF  -- Dummy example classes.  Rather make them small than to turn Checkstyle on.
    private static class StaticTester {
        protected static final Logger LOG = LogUtils.loggerForThisClass();
    }

    private static class NonStaticTester {
        @SuppressWarnings("unused")
        private final Logger log = LogUtils.loggerForThisClass();
    }
    // CHECKSTYLE:ON

}
