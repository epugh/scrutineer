package com.aconex.scrutineer2;

public interface ConnectorConfig {
    IdAndVersionStreamConnector createConnector(IdAndVersionFactory idAndVersionFactory);
}
