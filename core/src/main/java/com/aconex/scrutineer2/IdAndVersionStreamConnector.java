package com.aconex.scrutineer2;

import java.io.Closeable;
import java.util.Iterator;

/**
 * Connects to a provider (e.g: jdbc, elasticsearch) to create a Stream
 */
public interface IdAndVersionStreamConnector extends Closeable {
    void open();
    Iterator<IdAndVersion> stream();
}
