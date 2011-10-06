package com.aconex.scrutineer;

public class IdAndVersionStreamVerifier {

    public void verify(IdAndVersionStream primaryStream, IdAndVersionStream secondayStream) {
        primaryStream.open();
        secondayStream.open();
        try {

        }
        finally {
            primaryStream.close();
            secondayStream.close();
        }
    }
}
