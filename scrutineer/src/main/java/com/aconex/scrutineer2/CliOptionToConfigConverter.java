package com.aconex.scrutineer2;

import com.aconex.scrutineer2.config.CliConfig;
import com.aconex.scrutineer2.elasticsearch.ElasticSearchConnectorConfig;
import com.aconex.scrutineer2.jdbc.JdbcConnectorConfig;

public class CliOptionToConfigConverter {
    public CliConfig convert(ScrutineerCommandLineOptions options) {
        return new V1(options);
    }
    static class V1 implements CliConfig{
        private final ScrutineerCommandLineOptions options;

        V1(ScrutineerCommandLineOptions options) {
            this.options = options;
        }

        @Override
        public boolean numeric() {
            return options.numeric;
        }

        @Override
        public boolean versionsAsTimestamps() {
            return options.versionsAsTimestamps;
        }

        @Override
        public boolean ignoreTimestampsDuringRun() {
            return options.ignoreTimestampsDuringRun;
        }

        @SuppressWarnings("PMD.NcssMethodCount")
        @Override
        public JdbcConnectorConfig getPrimaryConnectorConfig() {
            JdbcConnectorConfig config = new JdbcConnectorConfig();
            config.setDriverClass(options.jdbcDriverClass);
            config.setJdbcUrl(options.jdbcURL);
            config.setUser(options.jdbcUser);
            config.setPassword(options.jdbcPassword);
            config.setSql(options.sql);
            return config;
        }
        @SuppressWarnings("PMD.NcssMethodCount")
        @Override
        public ElasticSearchConnectorConfig getSecondaryConnectorConfig() {
            ElasticSearchConnectorConfig config = new ElasticSearchConnectorConfig();
            config.setClusterName(options.clusterName);
            config.setHosts(options.elasticSearchHosts);
            config.setIndexName(options.indexName);
            config.setUsername(options.esUsername);
            config.setPassword(options.esPassword);
            config.setSslVerificationMode(options.esSSLVerificationMode);
            config.setSslEnabled(options.esSSLEnabled);
            config.setQuery(options.query);
            return config;
        }

    }
}
