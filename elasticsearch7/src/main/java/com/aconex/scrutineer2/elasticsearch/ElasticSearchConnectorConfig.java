package com.aconex.scrutineer2.elasticsearch;

import com.aconex.scrutineer2.ConnectorConfig;
import com.aconex.scrutineer2.IdAndVersionFactory;
import com.aconex.scrutineer2.IdAndVersionStreamConnector;
import org.elasticsearch.common.transport.TransportAddress;

import java.util.List;

public class ElasticSearchConnectorConfig implements ConnectorConfig {
    private String clusterName;
    private List<TransportAddress> hosts;
    private String indexName;
    private String query;

    // optional
    private String username;
    private String password;
    private String sslVerificationMode = "certificate";
    private boolean sslEnabled = false;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public List<TransportAddress> getHosts() {
        return hosts;
    }

    public void setHosts(String hostsSeparatedByComma) {
        this.hosts = new TransportAddressParser().convert(hostsSeparatedByComma);
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
