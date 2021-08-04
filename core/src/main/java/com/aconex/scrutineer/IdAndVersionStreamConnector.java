package com.aconex.scrutineer;

import java.util.Map;

/**
 * Connects to a provider (e.g: jdbc, elasticsearch) to create a Stream
 */
public interface IdAndVersionStreamConnector {
    /**
     * Required property which specify the name of the stream connector class
     */
    String STREAM_CONNECTOR_CLASS = "stream.connector.class";

    void configure(Map<String, String> props);
    IdAndVersionStream create(IdAndVersionFactory idAndVersionFactory);

    void close();
}
