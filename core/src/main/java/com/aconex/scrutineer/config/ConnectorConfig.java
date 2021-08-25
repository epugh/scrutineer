package com.aconex.scrutineer.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for configurations to extend.
 * <p>
 * This class holds both the original configuration that was provided as well as the parsed
 */
public abstract class ConnectorConfig {
    /**
     * Required property which specify the name of the stream connector class
     */
    public static final String STREAM_CONNECTOR_CLASS = "stream.connector.class";

    private Map<String, String> configs;

    protected ConnectorConfig(Map<String, String> props) {
        this.configs = new HashMap<>(props);
    }

    protected String get(String key) {
        return configs.get(key);
    }

    @Override
    public String toString() {
        return "ConnectorConfig{" +
                "configs=" + configs +
                '}';
    }
}
