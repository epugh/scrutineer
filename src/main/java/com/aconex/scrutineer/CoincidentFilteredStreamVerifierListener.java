package com.aconex.scrutineer;

import com.aconex.scrutineer.javautil.SystemTimeSource;
import com.aconex.scrutineer.javautil.TimeSource;
import org.joda.time.format.ISODateTimeFormat;

public class CoincidentFilteredStreamVerifierListener implements IdAndVersionStreamVerifierListener {

    private static final org.slf4j.Logger LOGGER = LogUtils.loggerForThisClass();

    private final IdAndVersionStreamVerifierListener otherListener;
    private final long runStartTime;

    public CoincidentFilteredStreamVerifierListener(IdAndVersionStreamVerifierListener otherListener) {
        this(new SystemTimeSource(), otherListener);
    }

    public CoincidentFilteredStreamVerifierListener(TimeSource timeSource, IdAndVersionStreamVerifierListener otherListener) {
        this.otherListener = otherListener;
        this.runStartTime = timeSource.getCurrentTime();
        LogUtils.info(LOGGER, "Will suppress any inconsistency detected on or after %s", ISODateTimeFormat.dateTime().print(runStartTime));
    }

    @Override
    public void onStreamComparison(IdAndVersion primaryItem, IdAndVersion secondaryItem) {
        otherListener.onStreamComparison(primaryItem, secondaryItem);
    }

    @Override
    public void onMissingInSecondaryStream(IdAndVersion idAndVersion) {
        if (idAndVersion.getVersion() < runStartTime) {
            otherListener.onMissingInSecondaryStream(idAndVersion);
        }
    }

    @Override
    public void onMissingInPrimaryStream(IdAndVersion idAndVersion) {
        if (idAndVersion.getVersion() < runStartTime) {
            otherListener.onMissingInPrimaryStream(idAndVersion);
        }
    }

    @Override
    public void onVersionMisMatch(IdAndVersion primaryItem, IdAndVersion secondaryItem) {
        if (primaryItem.getVersion() < runStartTime && secondaryItem.getVersion() < runStartTime) {
            otherListener.onVersionMisMatch(primaryItem, secondaryItem);
        }
    }

    @Override
    public void onVerificationStarted() {
        otherListener.onVerificationStarted();
    }
    @Override
    public void onVerificationCompleted() {
        otherListener.onVerificationCompleted();
    }
}
