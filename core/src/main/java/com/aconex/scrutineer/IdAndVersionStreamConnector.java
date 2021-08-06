package com.aconex.scrutineer;

import java.util.Map;

/**
 * Connects to a provider (e.g: jdbc, elasticsearch) to create a Stream
 */
public interface IdAndVersionStreamConnector {
    void configure(Map<String, String> props);
    IdAndVersionStream create(IdAndVersionFactory idAndVersionFactory);

    void close();
}
