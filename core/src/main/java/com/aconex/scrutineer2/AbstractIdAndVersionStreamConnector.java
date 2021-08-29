package com.aconex.scrutineer2;

import com.aconex.scrutineer2.javautil.JavaIteratorIdAndVersionStream;
import com.fasterxml.sort.SortConfig;
import com.fasterxml.sort.Sorter;

import java.io.IOException;

public abstract class AbstractIdAndVersionStreamConnector implements IdAndVersionStreamConnector {
    private static final int DEFAULT_SORT_MEM = 256 * 1024 * 1024;
    private final ConnectorConfig connectorConfig;
    private final IdAndVersionFactory idAndVersionFactory;
    private final Sorter<IdAndVersion> sorter;

    protected AbstractIdAndVersionStreamConnector(ConnectorConfig connectorConfig, IdAndVersionFactory idAndVersionFactory) {
        this.connectorConfig = connectorConfig;
        this.idAndVersionFactory = idAndVersionFactory;
        SortConfig sortConfig = new SortConfig().withMaxMemoryUsage(DEFAULT_SORT_MEM);
        sorter = new Sorter<>(sortConfig);
    }
    protected abstract IdAndVersionStream fetchFromSource();

    @Override
    public IdAndVersionStream stream() {
        IdAndVersionStream idAndVersionStream = fetchFromSource();
        if(connectorConfig.isPresorted()){
            return idAndVersionStream;
        } else {
            return sort(idAndVersionStream);
        }

    }

    private IdAndVersionStream sort(IdAndVersionStream idAndVersionStream) {
        try {
            return  new JavaIteratorIdAndVersionStream(sorter.sort(new IdAndVersionCollectionStreamDataReader(idAndVersionStream.iterator())));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to sort IdAndVersionStream", e);
        }
    }

    protected IdAndVersionFactory getIdAndVersionFactory() {
        return idAndVersionFactory;
    }

    protected ConnectorConfig getConnectorConfig() {
        return connectorConfig;
    }
}
