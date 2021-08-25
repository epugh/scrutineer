package com.aconex.scrutineer;

import java.io.Closeable;
import java.util.Map;

/**
 * Connects to a provider (e.g: jdbc, elasticsearch) to create a Stream
 */
public interface IdAndVersionStreamConnector extends Closeable {
    void configure(Map<String, String> props);
    IdAndVersionStream connect(IdAndVersionFactory idAndVersionFactory);
}
