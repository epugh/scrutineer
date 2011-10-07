package com.aconex.scrutineer;

import java.util.Iterator;

public class IdAndVersionStreamVerifier {

    public void verify(IdAndVersionStream primaryStream, IdAndVersionStream secondayStream, IdAndVersionStreamVerifierListener idAndVersionStreamVerifierListener) {
        primaryStream.open();
        secondayStream.open();
        try {
            Iterator<IdAndVersion> primaryIterator = primaryStream.iterator();
            Iterator<IdAndVersion> secondaryIterator = secondayStream.iterator();
            
            while (primaryIterator.hasNext() && secondaryIterator.hasNext()) {
                IdAndVersion primaryItem = primaryIterator.next();
                IdAndVersion secondaryItem = secondaryIterator.next();

                if(!primaryItem.equals(secondaryItem)) {
                    if (primaryItem.getId().equals(secondaryItem.getId())) {
                        idAndVersionStreamVerifierListener.onVersionMisMatch(primaryItem, secondaryItem);
                    }
                    else if (primaryItem.compareTo(secondaryItem) < 0) {
                        idAndVersionStreamVerifierListener.onMissingInSecondaryStream(primaryItem);
                        primaryIterator.next();
                    }
                    else {
                        idAndVersionStreamVerifierListener.onMissingInPrimaryStream(secondaryItem);
                        secondaryIterator.next();
                    }
                }
            }

            handleDifferencesAtEndOfStreams(idAndVersionStreamVerifierListener, primaryIterator, secondaryIterator);
        }
        finally {
            primaryStream.close();
            secondayStream.close();
        }
    }

    private void handleDifferencesAtEndOfStreams(IdAndVersionStreamVerifierListener idAndVersionStreamVerifierListener, Iterator<IdAndVersion> primaryIterator, Iterator<IdAndVersion> secondaryIterator) {

        while (primaryIterator.hasNext()) {
            idAndVersionStreamVerifierListener.onMissingInSecondaryStream(primaryIterator.next());
        }

        while (secondaryIterator.hasNext()) {
            idAndVersionStreamVerifierListener.onMissingInPrimaryStream(secondaryIterator.next());
        }
    }
}
