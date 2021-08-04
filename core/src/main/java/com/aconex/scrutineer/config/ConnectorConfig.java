package com.aconex.scrutineer.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for configurations to extend.
 * <p>
 * This class holds both the original configuration that was provided as well as the parsed (TODO: implement PropDefinition and passed props)
 */
public abstract class ConnectorConfig {
    private Map<String, String> configs;

    // TODO: use PropDefinition which has type information to parse the original properties
    protected ConnectorConfig(Map<String, String> props) {
        this.configs = new HashMap<>(props);
    }

    protected String get(String key) {
        return configs.get(key);
    }
}
