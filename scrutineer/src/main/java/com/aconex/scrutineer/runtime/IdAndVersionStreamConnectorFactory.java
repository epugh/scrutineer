package com.aconex.scrutineer.runtime;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import java.util.Map;

import com.aconex.scrutineer.IdAndVersionStreamConnector;
import com.aconex.scrutineer.config.ConfigurationProvider;
import com.aconex.scrutineer.config.ConnectorConfig;
import org.apache.commons.lang3.tuple.Pair;

public class IdAndVersionStreamConnectorFactory {
    private final ConfigurationProvider configurationProvider;
    private final StreamConnectorPlugins streamConnectorPlugins;

    public IdAndVersionStreamConnectorFactory(ConfigurationProvider configurationProvider, StreamConnectorPlugins streamConnectorPlugins) {
        this.configurationProvider = configurationProvider;
        this.streamConnectorPlugins = streamConnectorPlugins;
    }

    /**
     * Return a pair of primary (jdbc) and secondary (elasticsearch) stream connectors
     */
    @SuppressWarnings("PMD.CloseResource")
    public Pair<IdAndVersionStreamConnector, IdAndVersionStreamConnector> createStreamConnectors() {
        IdAndVersionStreamConnector primaryConnector =
                newConfiguredConnector(configurationProvider.getPrimaryConnectorConfigs());

        IdAndVersionStreamConnector secondaryConnector =
                newConfiguredConnector(configurationProvider.getSecondaryConnectorConfigs());

        return Pair.of(primaryConnector, secondaryConnector);
    }

    private IdAndVersionStreamConnector newConfiguredConnector(Map<String, String> configs) {
        String connectorClass = checkNotNull(configs.get(ConnectorConfig.STREAM_CONNECTOR_CLASS),
                format("'%s' must be provided in the connector properties", ConnectorConfig.STREAM_CONNECTOR_CLASS));

        IdAndVersionStreamConnector streamConnector = streamConnectorPlugins.newConnector(connectorClass);
        streamConnector.configure(configs);

        return streamConnector;
    }
}
