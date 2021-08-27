package com.aconex.scrutineer2;

import java.util.Iterator;

public class DeletionVerifier {
    private final IdAndVersionStream primaryStream;
    private final ExistenceChecker existenceChecker;
    private final IdAndVersionStreamVerifierListener listener;

    public DeletionVerifier(IdAndVersionStream primaryStream, ExistenceChecker existenceChecker, IdAndVersionStreamVerifierListener listener) {
        this.primaryStream = primaryStream;
        this.existenceChecker = existenceChecker;
        this.listener = listener;
    }

    public void verify() {
        primaryStream.open();

        try {
            Iterator<IdAndVersion> iterator = primaryStream.iterator();
            iterateAndCheck(iterator);

        } finally {
            primaryStream.close();
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
