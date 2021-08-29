package com.aconex.scrutineer2;

import org.slf4j.Logger;

import java.io.IOException;
import java.util.Iterator;

public class DeletionVerifier {
    private static final Logger LOG = LogUtils.loggerForThisClass();
    private final IdAndVersionStreamConnector primaryStreamConnector;
    private final ExistenceChecker existenceChecker;
    private final IdAndVersionStreamVerifierListener listener;

    public DeletionVerifier(IdAndVersionStreamConnector primaryStreamConnector, ExistenceChecker existenceChecker, IdAndVersionStreamVerifierListener listener) {
        this.primaryStreamConnector = primaryStreamConnector;
        this.existenceChecker = existenceChecker;
        this.listener = listener;
    }

    public void verify() {
        try {
            primaryStreamConnector.open();
            iterateAndCheck(primaryStreamConnector.stream().iterator());
        } finally {
            closeQuietly(primaryStreamConnector);
        }
    }

    private void closeQuietly(IdAndVersionStreamConnector streamConnector) {
        try {
            streamConnector.close();
        } catch (IOException e) {
            LOG.warn("Failed to close IdAndVersionStreamConnector.");
        }
    }

    private void iterateAndCheck(Iterator<IdAndVersion> iterator) {
        while (iterator.hasNext()) {
            IdAndVersion idAndVersion = iterator.next();
            if (existenceChecker.exists(idAndVersion)) {
                listener.onMissingInPrimaryStream(idAndVersion);
            }
        }
    }
}
