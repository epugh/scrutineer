package com.aconex.scrutineer;

import org.apache.log4j.Logger;

import java.util.Iterator;

public class IdAndVersionStreamVerifier {

    private static final Logger LOG = LogUtils.loggerForThisClass();

    //CHECKSTYLE:OFF
    @SuppressWarnings("PMD.NcssMethodCount")
    public void verify(IdAndVersionStream primaryStream, IdAndVersionStream secondayStream, IdAndVersionStreamVerifierListener idAndVersionStreamVerifierListener) {
        try {
            primaryStream.open();
            secondayStream.open();
            
            Iterator<IdAndVersion> primaryIterator = primaryStream.iterator();
            Iterator<IdAndVersion> secondaryIterator = secondayStream.iterator();

            IdAndVersion primaryItem =  next(primaryIterator);
            IdAndVersion secondaryItem = next(secondaryIterator);

            while (primaryItem != null && secondaryItem != null) {
                if (primaryItem.equals(secondaryItem)) {
                    primaryItem =  next(primaryIterator);
                    secondaryItem = next(secondaryIterator);
                }
                else if (primaryItem.getId().equals(secondaryItem.getId())) {
                    idAndVersionStreamVerifierListener.onVersionMisMatch(primaryItem, secondaryItem);
                    primaryItem = next(primaryIterator);
                    secondaryItem = next(secondaryIterator);
                }
                else if (primaryItem.compareTo(secondaryItem) < 0) {
                    idAndVersionStreamVerifierListener.onMissingInSecondaryStream(primaryItem);
                    primaryItem = next(primaryIterator);
                }
                else {
                    idAndVersionStreamVerifierListener.onMissingInPrimaryStream(secondaryItem);
                    secondaryItem = next(secondaryIterator);
                }
            }

            while (primaryItem != null) {
                idAndVersionStreamVerifierListener.onMissingInSecondaryStream(primaryItem);
                primaryItem = next(primaryIterator);
            }

            while (secondaryItem != null) {
                idAndVersionStreamVerifierListener.onMissingInPrimaryStream(secondaryItem);
                secondaryItem = next(secondaryIterator);
            }
        }
        finally {
            closeWithoutThrowingException(primaryStream);
            closeWithoutThrowingException(secondayStream);
        }
    }
    //CHECKSTYLE:ON

    private IdAndVersion next(Iterator<IdAndVersion> iterator) {
        if (iterator.hasNext()) {
            return iterator.next();
        }
        else {
            return null;
        }
    }

    private void closeWithoutThrowingException(IdAndVersionStream idAndVersionStream) {
        try {
            idAndVersionStream.close();
        }
        catch(Exception e) {
            LogUtils.warn(LOG,"Unable to close IdAndVersionStream",e);
        }
    }

}
