package com.aconex.scrutineer.elasticsearch;

import java.util.Map;

import com.aconex.scrutineer.IdAndVersion;
import com.aconex.scrutineer.IdAndVersionFactory;
import com.aconex.scrutineer.IdAndVersionStream;
import com.aconex.scrutineer.IdAndVersionStreamConnector;
import com.fasterxml.sort.DataReaderFactory;
import com.fasterxml.sort.DataWriterFactory;
import com.fasterxml.sort.SortConfig;
import com.fasterxml.sort.Sorter;
import com.fasterxml.sort.util.NaturalComparator;
import org.apache.commons.lang3.SystemUtils;
import org.elasticsearch.client.Client;

public class ElasticSearchStreamConnector implements IdAndVersionStreamConnector {
    private static final int DEFAULT_SORT_MEM = 256 * 1024 * 1024;

    private Client client;
    private ElasticSearchConnectorConfig config;

    @Override
    public void configure(Map<String, String> props) {
        this.config = new ElasticSearchConnectorConfig(props);
    }

    @Override
    public IdAndVersionStream create(IdAndVersionFactory idAndVersionFactory) {
        try {
            this.client = new ElasticSearchTransportClientFactory().getTransportClient(this.config);
            return new ElasticSearchIdAndVersionStream(new ElasticSearchDownloader(client, config.getIndexName(), config.getEsQuery(), idAndVersionFactory), new ElasticSearchSorter(createSorter(idAndVersionFactory)), new IteratorFactory(idAndVersionFactory), SystemUtils.getJavaIoTmpDir().getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        closeElasticSearchConnections();
    }

    void closeElasticSearchConnections() {
        if (client != null) {
            client.close();
        }
    }

    private Sorter<IdAndVersion> createSorter(IdAndVersionFactory idAndVersionFactory) {
        SortConfig sortConfig = new SortConfig().withMaxMemoryUsage(DEFAULT_SORT_MEM);
        DataReaderFactory<IdAndVersion> dataReaderFactory = new IdAndVersionDataReaderFactory(idAndVersionFactory);
        DataWriterFactory<IdAndVersion> dataWriterFactory = new IdAndVersionDataWriterFactory();
        return new Sorter<IdAndVersion>(sortConfig, dataReaderFactory, dataWriterFactory, new NaturalComparator<IdAndVersion>());
    }

}
