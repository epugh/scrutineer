package com.aconex.scrutineer2.v2.configconverter;

import com.aconex.scrutineer2.http.HttpConnectorConfig;

import java.util.Map;

public class HttpConnectorConfigConverter extends ConnectorConfigConverter{
        private static final String CONFIG_HTTP_ENDPOINT_URL = "http.endpoint.url";
        private static final String CONFIG_HTTP_CONNECTION_TIMEOUT = "http.connection.timeout";
        private static final String CONFIG_HTTP_READ_TIMEOUT = "http.read.timeout";

        @SuppressWarnings("PMD.NcssMethodCount")
        HttpConnectorConfig convert(Map<String, String> props) {
            HttpConnectorConfig config = new HttpConnectorConfig();
            config.setHttpEndpointUrl(getRequiredProperty(props, CONFIG_HTTP_ENDPOINT_URL));
            if (props.containsKey(CONFIG_HTTP_CONNECTION_TIMEOUT)) {
                config.setHttpConnectionTimeoutInMillisecond(Integer.parseInt(props.get(CONFIG_HTTP_CONNECTION_TIMEOUT)));
            }
            if (props.containsKey(CONFIG_HTTP_READ_TIMEOUT)) {
                config.setHttpReadTimeoutInMillisecond(Integer.parseInt(props.get(CONFIG_HTTP_READ_TIMEOUT)));
            }
            return config;
        }
    }
