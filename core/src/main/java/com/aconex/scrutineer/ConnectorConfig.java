package com.aconex.scrutineer;

public abstract class ConnectorConfig {
    public abstract IdAndVersionStreamConnector createConnector(IdAndVersionFactory idAndVersionFactory);

    private boolean presorted;
    public boolean isPresorted() {
        return presorted;
    }
    public void setPresorted(boolean presorted) {
        this.presorted = presorted;
    }
}
