package com.aconex.scrutineer2;

import com.fasterxml.sort.SortConfig;
import com.fasterxml.sort.Sorter;

import java.io.IOException;
import java.util.Iterator;

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
    protected abstract Iterator<IdAndVersion> fetchFromSource();

    @Override
    public Iterator<IdAndVersion> stream() {
        Iterator<IdAndVersion> idAndVersionIterator = fetchFromSource();
        if(connectorConfig.isPresorted()){
            return idAndVersionIterator;
        } else {
            return sort(idAndVersionIterator);
        }

    }

    private Iterator<IdAndVersion> sort(Iterator<IdAndVersion> idAndVersionIterator) {
        try {
            return  sorter.sort(new IdAndVersionCollectionStreamDataReader(idAndVersionIterator));
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
