package com.aconex.scrutineer.runtime;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.aconex.scrutineer.IdAndVersionStreamConnector;
import com.aconex.scrutineer.elasticsearch.v7.ElasticSearchStreamConnector;
import com.aconex.scrutineer.jdbc.JdbcStreamConnector;

public class StreamConnectorPlugins {

    private static final Map<String, Supplier<IdAndVersionStreamConnector>> PLUGIN_PROVIDERS = new LinkedHashMap<>();
    static {
        PLUGIN_PROVIDERS.put(JdbcStreamConnector.class.getName(), () -> new JdbcStreamConnector());
        PLUGIN_PROVIDERS.put(ElasticSearchStreamConnector.class.getName(), () -> new ElasticSearchStreamConnector());
    }

    public IdAndVersionStreamConnector newConnector(String connectorClass) {
        Supplier<IdAndVersionStreamConnector> streamConnectorSupplier = PLUGIN_PROVIDERS.get(connectorClass);
        if (streamConnectorSupplier == null) {
            throw new IllegalArgumentException("Fail to find any connector class that implements IdAndVersionStreamConnector that matches " + connectorClass + ", available connectors are: " + getAvailableConnectorPluginNames());
        }

        return streamConnectorSupplier.get();
    }

    private String getAvailableConnectorPluginNames() {
        return PLUGIN_PROVIDERS.keySet().stream().collect(Collectors.joining(","));
    }
}
