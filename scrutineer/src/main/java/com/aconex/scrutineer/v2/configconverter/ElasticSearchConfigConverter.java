package com.aconex.scrutineer.v2.configconverter;

import com.aconex.scrutineer.elasticsearch.v7.ElasticSearchStreamConnector;
import com.aconex.scrutineer.elasticsearch.v7.TransportAddressParser;

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
        ElasticSearchStreamConnector.Config convert(Map<String, String> props) {
            ElasticSearchStreamConnector.Config config = new ElasticSearchStreamConnector.Config();
            config.setClusterName(getRequiredProperty(props, CONFIG_ES_CLUSTER_NAME));
            config.setHosts(new TransportAddressParser().convert(getRequiredProperty(props, CONFIG_ES_HOSTS)));
            config.setIndexName(getRequiredProperty(props, CONFIG_ES_INDEX_NAME));
            config.setQuery(getRequiredProperty(props, CONFIG_ES_QUERY));

            config.setUsername(props.get(CONFIG_ES_USERNAME));
            config.setPassword(props.get(CONFIG_ES_PASSWORD));
            config.setSslVerificationMode(CONFIG_ES_SSL_VERIFICATION_MODE);
            config.setSslEnabled(Boolean.parseBoolean(props.get(CONFIG_ES_SSL_ENABLED)));
            return config;
        }
    }

