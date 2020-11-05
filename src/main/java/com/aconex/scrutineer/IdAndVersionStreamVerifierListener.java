package com.aconex.scrutineer;

public interface IdAndVersionStreamVerifierListener {

    void onMissingInSecondaryStream(IdAndVersion idAndVersion);

    void onMissingInPrimaryStream(IdAndVersion idAndVersion);

    void onVersionMisMatch(IdAndVersion primaryItem, IdAndVersion secondaryItem);
    default void onStreamComparison(IdAndVersion primaryItem, IdAndVersion secondaryItem) {}
    default void onVerificationStarted() {}
    default void onVerificationCompleted() {}
}
