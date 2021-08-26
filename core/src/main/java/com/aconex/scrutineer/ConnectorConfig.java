package com.aconex.scrutineer;

public interface ConnectorConfig {
    IdAndVersionStreamConnector createConnector(IdAndVersionFactory idAndVersionFactory);
}
