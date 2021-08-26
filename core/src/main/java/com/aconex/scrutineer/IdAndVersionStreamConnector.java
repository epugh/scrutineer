package com.aconex.scrutineer;

import java.io.Closeable;

/**
 * Connects to a provider (e.g: jdbc, elasticsearch) to create a Stream
 */
public interface IdAndVersionStreamConnector extends Closeable {
    IdAndVersionStream connect();
}
