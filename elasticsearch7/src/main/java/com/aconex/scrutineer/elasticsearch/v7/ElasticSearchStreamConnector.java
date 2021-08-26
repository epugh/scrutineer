package com.aconex.scrutineer.elasticsearch.v7;

import com.aconex.scrutineer.ConnectorConfig;
import com.aconex.scrutineer.IdAndVersion;
import com.aconex.scrutineer.IdAndVersionFactory;
import com.aconex.scrutineer.IdAndVersionStream;
import com.aconex.scrutineer.IdAndVersionStreamConnector;
import com.aconex.scrutineer.elasticsearch.ElasticSearchIdAndVersionStream;
import com.aconex.scrutineer.elasticsearch.ElasticSearchSorter;
import com.aconex.scrutineer.elasticsearch.IdAndVersionDataReaderFactory;
import com.aconex.scrutineer.elasticsearch.IdAndVersionDataWriterFactory;
import com.aconex.scrutineer.elasticsearch.IteratorFactory;
import com.fasterxml.sort.DataReaderFactory;
import com.fasterxml.sort.DataWriterFactory;
import com.fasterxml.sort.SortConfig;
import com.fasterxml.sort.Sorter;
import com.fasterxml.sort.util.NaturalComparator;
import org.apache.commons.lang3.SystemUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.transport.TransportAddress;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ElasticSearchStreamConnector implements IdAndVersionStreamConnector {
    private static final int DEFAULT_SORT_MEM = 256 * 1024 * 1024;
    public static final String WORKING_DIRECTORY_PREFIX = "elastisearch-stream-";

    private Client client;
    private final Config config;
    private final IdAndVersionFactory idAndVersionFactory;

    public ElasticSearchStreamConnector(Config config,IdAndVersionFactory idAndVersionFactory) {
        this.config = config;
        this.idAndVersionFactory = idAndVersionFactory;
    }


    @Override
    public IdAndVersionStream connect() {
        try {
            this.client = new ElasticSearchTransportClientFactory().getTransportClient(this.config);
            return new ElasticSearchIdAndVersionStream(
                    new ElasticSearchDownloader(client, config.getIndexName(), config.getQuery(), idAndVersionFactory),
                    new ElasticSearchSorter(createSorter(idAndVersionFactory)),
                    new IteratorFactory(idAndVersionFactory),
                    createWorkingDirectory()
            );
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
        return new Sorter<>(sortConfig, dataReaderFactory, dataWriterFactory, new NaturalComparator<IdAndVersion>());
    }

    private String createWorkingDirectory() throws IOException {
        Path javaIoTempDir = SystemUtils.getJavaIoTmpDir().toPath();
        return Files.createTempDirectory(javaIoTempDir, WORKING_DIRECTORY_PREFIX).toFile().getAbsolutePath();
    }
    public static class Config implements ConnectorConfig {
        private String clusterName;
        private List<TransportAddress> hosts;
        private String indexName;
        private String query;
        private String username;
        private String password;
        private String sslVerificationMode;
        private boolean sslEnabled;

        public String getClusterName() {
            return clusterName;
        }

        public void setClusterName(String clusterName) {
            this.clusterName = clusterName;
        }

        public List<TransportAddress> getHosts() {
            return hosts;
        }

        public void setHosts(List<TransportAddress> hosts) {
            this.hosts = hosts;
        }

        public String getIndexName() {
            return indexName;
        }

        public void setIndexName(String indexName) {
            this.indexName = indexName;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getSslVerificationMode() {
            return sslVerificationMode;
        }

        public void setSslVerificationMode(String sslVerificationMode) {
            this.sslVerificationMode = sslVerificationMode;
        }

        public boolean isSslEnabled() {
            return sslEnabled;
        }

        public void setSslEnabled(boolean sslEnabled) {
            this.sslEnabled = sslEnabled;
        }

        @Override
        public IdAndVersionStreamConnector createConnector(IdAndVersionFactory idAndVersionFactory) {
            return new ElasticSearchStreamConnector(this, idAndVersionFactory);
        }
    }
}
