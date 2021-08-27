package com.aconex.scrutineer2.elasticsearch;

import java.util.List;

import org.elasticsearch.common.transport.TransportAddress;

@SuppressWarnings("PMD.NcssMethodCount")
public class ElasticSearchConnectionConfig {
    private String clusterName;
    private List<TransportAddress> elasticSearchHosts;
    private String indexName;
    private String query;

    // optionals
    private String esUsername;
    private String esPassword;
    private String esSSLVerificationMode = "certificate";
    private boolean esSSLEnabled = false;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public List<TransportAddress> getElasticSearchHosts() {
        return elasticSearchHosts;
    }

    public void setElasticSearchHosts(List<TransportAddress> elasticSearchHosts) {
        this.elasticSearchHosts = elasticSearchHosts;
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

    public String getEsUsername() {
        return esUsername;
    }

    public void setEsUsername(String esUsername) {
        this.esUsername = esUsername;
    }

    public String getEsPassword() {
        return esPassword;
    }

    public void setEsPassword(String esPassword) {
        this.esPassword = esPassword;
    }

    public String getEsSSLVerificationMode() {
        return esSSLVerificationMode;
    }

    public void setEsSSLVerificationMode(String esSSLVerificationMode) {
        this.esSSLVerificationMode = esSSLVerificationMode;
    }

    public boolean isEsSSLEnabled() {
        return esSSLEnabled;
    }

    public void setEsSSLEnabled(boolean esSSLEnabled) {
        this.esSSLEnabled = esSSLEnabled;
    }
}
