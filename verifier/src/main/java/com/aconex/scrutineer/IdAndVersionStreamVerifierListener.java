package com.aconex.scrutineer;

import com.aconex.scrutineer.IdAndVersion;

public interface IdAndVersionStreamVerifierListener {

    void onMissingInSecondaryStream(IdAndVersion idAndVersion);

    void onMissingInPrimaryStream(IdAndVersion idAndVersion);

    void onVersionMisMatch(IdAndVersion primaryItem, IdAndVersion secondaryItem);
    default void onStreamComparison(IdAndVersion primaryItem, IdAndVersion secondaryItem) {}
    default void onVerificationStarted() {}
    default void onVerificationCompleted() {}
}
