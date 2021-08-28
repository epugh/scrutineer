package com.aconex.scrutineer2.v2.configconverter;

import com.aconex.scrutineer2.elasticsearch.ElasticSearchConnectorConfig;

import java.util.Map;

public class ElasticSearchConfigConverter extends ConnectorConfigConverter{
        private static final String CONFIG_ES_CLUSTER_NAME = "es.cluster.name";
        private static final String CONFIG_ES_HOSTS = "es.hosts";
        private static final String CONFIG_ES_INDEX_NAME = "es.index.name";
        private static final String CONFIG_ES_QUERY = "es.query";
        private static final String CONFIG_ES_USERNAME = "es.username";
        private static final String CONFIG_ES_PASSWORD = "es.password";
        private static final String CONFIG_ES_SSL_VERIFICATION_MODE = "es.ssl.verification.mode";
        private static final String CONFIG_ES_SSL_ENABLED = "es.ssl.enabled";

        @SuppressWarnings("PMD.NcssMethodCount")
        ElasticSearchConnectorConfig convert(Map<String, String> props) {
            ElasticSearchConnectorConfig config = new ElasticSearchConnectorConfig();
            config.setClusterName(getRequiredProperty(props, CONFIG_ES_CLUSTER_NAME));
            config.setHosts(getRequiredProperty(props, CONFIG_ES_HOSTS));
            config.setIndexName(getRequiredProperty(props, CONFIG_ES_INDEX_NAME));
            config.setQuery(getRequiredProperty(props, CONFIG_ES_QUERY));

            config.setUsername(props.get(CONFIG_ES_USERNAME));
            config.setPassword(props.get(CONFIG_ES_PASSWORD));
            config.setSslVerificationMode(CONFIG_ES_SSL_VERIFICATION_MODE);
            config.setSslEnabled(Boolean.parseBoolean(props.get(CONFIG_ES_SSL_ENABLED)));
            return config;
        }
    }

