package com.aconex.scrutineer2.v2.configconverter;

import com.aconex.scrutineer2.ConnectorConfig;
import com.aconex.scrutineer2.config.CliConfig;
import com.aconex.scrutineer2.v2.ScrutineerCommandLineOptionsV2;
import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import static java.lang.String.format;

public class CliOptionV2ToConfigConverter {
    public CliConfig convert(ScrutineerCommandLineOptionsV2 options) {
        return new V2(options);
    }

    static class V2 implements CliConfig {
        private final ScrutineerCommandLineOptionsV2 options;

        V2(ScrutineerCommandLineOptionsV2 options) {
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

        @Override
        public ConnectorConfig getPrimaryConnectorConfig() {
            return convertToConnectorConfig(readPropertyFromClassPath(options.primaryConfig));
        }

        @Override
        public ConnectorConfig getSecondaryConnectorConfig() {
            return convertToConnectorConfig(readPropertyFromClassPath(options.secondaryConfig));
        }

        private static final String CONFIG_SOURCE_TYPE = "source.type";
        private static final String SOURCE_TYPE_JDBC = "jdbc";
        private static final String SOURCE_TYPE_ELASTICSEARCH = "elasticsearch";
        private static final String SOURCE_TYPE_HTTP = "http";

        private static final Map<String, Function<Map<String, String>, ConnectorConfig>> CONFIG_CONVERTER_MAPPING =
                new HashMap<String, Function<Map<String, String>, ConnectorConfig>>() {{
                    put(SOURCE_TYPE_JDBC, (props) -> new JdbcConnectorConfigConverter().convert(props));
                    put(SOURCE_TYPE_ELASTICSEARCH, (props) -> new ElasticSearchConnectorConfigConverter().convert(props));
                    put(SOURCE_TYPE_HTTP, (props) -> new HttpConnectorConfigConverter().convert(props));
                }};

        private ConnectorConfig convertToConnectorConfig(Map<String, String> props) {
            String sourceType = props.get(CONFIG_SOURCE_TYPE);
            if (CONFIG_CONVERTER_MAPPING.containsKey(sourceType)) {
                return CONFIG_CONVERTER_MAPPING.get(sourceType).apply(props);
            } else {
                throw new IllegalStateException(String.format("Unsupported source type: %s. Supported are: %s",
                        sourceType, CONFIG_CONVERTER_MAPPING.keySet()));
            }
        }

        @SuppressWarnings("PMD.NcssMethodCount")
        private Map<String, String> readPropertyFromClassPath(String configProperty) {
            try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(configProperty)) {
                if (is == null) {
                    throw new IllegalArgumentException(format("config property file: '%s' not found in classpath, stream config files should be placed under 'config' folder", configProperty));
                }

                Properties properties = new Properties();
                properties.load(is);
                return ImmutableMap.copyOf(new HashMap(properties));
            } catch (IOException e) {
                throw new RuntimeException(format("fail to load config property file: '%s'", configProperty), e);
            }
        }
    }

}
