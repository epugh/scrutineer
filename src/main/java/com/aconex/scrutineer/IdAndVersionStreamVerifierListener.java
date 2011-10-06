package com.aconex.scrutineer;

public interface IdAndVersionStreamVerifierListener {

    void onMissingInSecondaryStream(IdAndVersion idAndVersion);
    
}
