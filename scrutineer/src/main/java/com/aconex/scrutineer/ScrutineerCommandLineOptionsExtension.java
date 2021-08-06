package com.aconex.scrutineer;

import java.util.HashMap;
import java.util.Map;

import com.aconex.scrutineer.config.ConfigurationProvider;
import com.aconex.scrutineer.config.ConnectorConfig;
import com.aconex.scrutineer.elasticsearch.v7.ElasticSearchConnectorConfig;
import com.aconex.scrutineer.elasticsearch.v7.TransportAddressParser;
import com.aconex.scrutineer.jdbc.JdbcConnectorConfig;
import com.google.common.collect.ImmutableMap;

/**
 * Provide scrutineer and stream configurations via through a ScrutineerCommandLineOptions delegate, this class
 * "adapters" the legacy ScrutineerCommandLineOptions to the new extension framework, it helps us guarantee
 * the exist command line runner is still working
 */
@SuppressWarnings("PMD.NcssMethodCount")
public class ScrutineerCommandLineOptionsExtension implements ConfigurationProvider{

    private static final String JDBC_STREAM_CONNECTOR_CLASS = "com.aconex.scrutineer.jdbc.JdbcStreamConnector";
    private static final String ELASTICSEARCH_STREAM_CONNECTOR_CLASS = "com.aconex.scrutineer.elasticsearch.v7.ElasticSearchStreamConnector";

    private ScrutineerCommandLineOptions commandLineOptions;

    public ScrutineerCommandLineOptionsExtension(ScrutineerCommandLineOptions commandLineOptions) {
        this.commandLineOptions = commandLineOptions;
    }

    @Override
    public boolean numeric() {
        return this.commandLineOptions.numeric;
    }

    @Override
    public boolean versionsAsTimestamps() {
        return this.commandLineOptions.versionsAsTimestamps;
    }

    @Override
    public boolean ignoreTimestampsDuringRun() {
        return this.commandLineOptions.ignoreTimestampsDuringRun;
    }

    @Override
    public Map<String, String> getPrimaryConnectorConfigs() {
        Map<String, String> props = initProperties(JDBC_STREAM_CONNECTOR_CLASS);

        props.put(JdbcConnectorConfig.CONFIG_JDBC_DRIVER_CLASS, commandLineOptions.jdbcDriverClass);
        props.put(JdbcConnectorConfig.CONFIG_JDBC_URL, commandLineOptions.jdbcURL);
        props.put(JdbcConnectorConfig.CONFIG_JDBC_SQL, commandLineOptions.sql);
        props.put(JdbcConnectorConfig.CONFIG_JDBC_USER, commandLineOptions.jdbcUser);
        props.put(JdbcConnectorConfig.CONFIG_JDBC_PASSWORD, commandLineOptions.jdbcPassword);
        return ImmutableMap.copyOf(props);
    }

    @Override
    public Map<String, String> getSecondaryConnectorConfigs() {
        Map<String, String> props = initProperties(ELASTICSEARCH_STREAM_CONNECTOR_CLASS);

        props.put(ElasticSearchConnectorConfig.CONFIG_ES_CLUSTER_NAME, commandLineOptions.clusterName);
        props.put(ElasticSearchConnectorConfig.CONFIG_ES_HOSTS, new TransportAddressParser().toString(commandLineOptions.elasticSearchHosts));
        props.put(ElasticSearchConnectorConfig.CONFIG_ES_USERNAME, commandLineOptions.esUsername);
        props.put(ElasticSearchConnectorConfig.CONFIG_ES_PASSWORD, commandLineOptions.esPassword);
        props.put(ElasticSearchConnectorConfig.CONFIG_ES_SSL_VERIFICATION_MODE, commandLineOptions.esSSLVerificationMode);
        props.put(ElasticSearchConnectorConfig.CONFIG_ES_SSL_ENABLED, Boolean.toString(commandLineOptions.esSSLEnabled));
        props.put(ElasticSearchConnectorConfig.CONFIG_ES_INDEX_NAME, commandLineOptions.indexName);
        props.put(ElasticSearchConnectorConfig.CONFIG_ES_QUERY, commandLineOptions.query);

        return ImmutableMap.copyOf(props);
    }

    private Map<String, String> initProperties(String connectorClass) {
        Map<String, String> props = new HashMap<>();
        props.put(ConnectorConfig.STREAM_CONNECTOR_CLASS, connectorClass);
        return props;
    }
}
