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
                primaryIterator.next();
                secondaryIterator.next();
            }

            while (primaryIterator.hasNext()) {
                idAndVersionStreamVerifierListener.onMissingInSecondaryStream(primaryIterator.next());
            }
        }
        finally {
            primaryStream.close();
            secondayStream.close();
        }
    }
}
