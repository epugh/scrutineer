package com.aconex.scrutineer.elasticsearch.v7;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import com.aconex.scrutineer.config.ConnectorConfig;
import org.elasticsearch.common.transport.TransportAddress;

public class ElasticSearchConnectorConfig extends ConnectorConfig {

    public static final String CONFIG_ES_CLUSTER_NAME = "es.cluster.name";
    public static final String CONFIG_ES_HOSTS = "es.hosts";
    public static final String CONFIG_ES_INDEX_NAME = "es.index.name";
    public static final String CONFIG_ES_QUERY = "es.query";
    public static final String CONFIG_ES_USERNAME = "es.username";
    public static final String CONFIG_ES_PASSWORD = "es.password";
    public static final String CONFIG_ES_SSL_VERIFICATION_MODE = "es.ssl.verification.mode";
    public static final String CONFIG_ES_SSL_ENABLED = "es.ssl.enabled";

    public ElasticSearchConnectorConfig(Map<String, String> props) {
        super(props);
    }

    public String getClusterName() {
        return get(CONFIG_ES_CLUSTER_NAME);
    }

    public List<TransportAddress> getHosts() {
        return new TransportAddressParser().convert(get(CONFIG_ES_HOSTS));
    }

    public String getIndexName() {
        return checkNotNull(get(CONFIG_ES_INDEX_NAME), "Configuration for " + CONFIG_ES_INDEX_NAME + " is required");
    }

    public String getEsQuery() {
        return checkNotNull(get(CONFIG_ES_QUERY), "Configuration for " + CONFIG_ES_QUERY + " is required");
    }

    public String getEsUsername() {
        return get(CONFIG_ES_USERNAME);
    }

    public String getEsPassword() {
        return get(CONFIG_ES_PASSWORD);
    }

    public String getSslVerificationMode() {
        return get(CONFIG_ES_SSL_VERIFICATION_MODE);
    }

    public boolean isSslEnabled() {
        return Boolean.valueOf(get(CONFIG_ES_SSL_ENABLED));
    }
}
