package com.aconex.scrutineer;

import com.aconex.scrutineer.javautil.SystemTimeSource;
import com.aconex.scrutineer.javautil.TimeSource;

public class CoincidentFilteredStreamVerifierListener implements IdAndVersionStreamVerifierListener {
    private final IdAndVersionStreamVerifierListener otherListener;
    private final TimeSource timeSource;

    public CoincidentFilteredStreamVerifierListener(IdAndVersionStreamVerifierListener otherListener) {
        this(new SystemTimeSource(), otherListener);
    }

    public CoincidentFilteredStreamVerifierListener(TimeSource timeSource, IdAndVersionStreamVerifierListener otherListener) {
        this.timeSource = timeSource;
        this.otherListener = otherListener;
    }

    @Override
    public void onMissingInSecondaryStream(IdAndVersion idAndVersion) {
        long currentTime = timeSource.getCurrentTime();
        if (idAndVersion.getVersion() < currentTime) {
            otherListener.onMissingInSecondaryStream(idAndVersion);
        }
    }

    @Override
    public void onMissingInPrimaryStream(IdAndVersion idAndVersion) {
        long currentTime = timeSource.getCurrentTime();
        if (idAndVersion.getVersion() < currentTime) {
            otherListener.onMissingInPrimaryStream(idAndVersion);
        }
    }

    @Override
    public void onVersionMisMatch(IdAndVersion primaryItem, IdAndVersion secondaryItem) {
        long currentTime = timeSource.getCurrentTime();
        if (primaryItem.getVersion() < currentTime && secondaryItem.getVersion() < currentTime) {
            otherListener.onVersionMisMatch(primaryItem, secondaryItem);
        }
    }
}
