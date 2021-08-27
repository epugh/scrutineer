package com.aconex.scrutineer2;

import java.util.Collection;
import java.util.function.Consumer;

public class CompositeStreamVerifierListener implements IdAndVersionStreamVerifierListener {
    private final Collection<IdAndVersionStreamVerifierListener> listeners;

    public CompositeStreamVerifierListener(Collection<IdAndVersionStreamVerifierListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public void onStreamComparison(IdAndVersion primaryItem, IdAndVersion secondaryItem) {
        this.notifyListener(listener -> listener.onStreamComparison(primaryItem, secondaryItem));
    }

    @Override
    public void onMissingInSecondaryStream(IdAndVersion idAndVersion) {
        this.notifyListener(listener -> listener.onMissingInSecondaryStream(idAndVersion));
    }

    @Override
    public void onMissingInPrimaryStream(IdAndVersion idAndVersion) {
        this.notifyListener(listener -> listener.onMissingInPrimaryStream(idAndVersion));
    }

    @Override
    public void onVersionMisMatch(IdAndVersion primaryItem, IdAndVersion secondaryItem) {
        this.notifyListener(listener -> listener.onVersionMisMatch(primaryItem, secondaryItem));
    }

    @Override
    public void onVerificationStarted() {
        this.notifyListener(listener -> listener.onVerificationStarted());
    }

    @Override
    public void onVerificationCompleted() {
        this.notifyListener(listener -> listener.onVerificationCompleted());
    }

    private void notifyListener(Consumer<IdAndVersionStreamVerifierListener> consumer) {
        for (IdAndVersionStreamVerifierListener listener : this.listeners) {
            consumer.accept(listener);
        }
    }
}
