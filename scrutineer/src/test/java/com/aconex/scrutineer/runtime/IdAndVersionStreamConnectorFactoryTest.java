package com.aconex.scrutineer.runtime;

import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import com.aconex.scrutineer.IdAndVersionStreamConnector;
import com.aconex.scrutineer.config.ConfigurationProvider;
import com.aconex.scrutineer.config.ConnectorConfig;
import com.aconex.scrutineer.elasticsearch.ElasticSearchStreamConnector;
import com.aconex.scrutineer.jdbc.JdbcStreamConnector;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
public class IdAndVersionStreamConnectorFactoryTest {
    @Mock
    private ConfigurationProvider configurationProvider;

    @Mock
    private StreamConnectorPlugins streamConnectorPlugins;

    @Mock
    private IdAndVersionStreamConnector primaryStreamConnector;

    @Mock
    private IdAndVersionStreamConnector secondaryStreamConnector;

    private String primaryConnectorClass = JdbcStreamConnector.class.getName();
    private String secondaryConnectorClass = ElasticSearchStreamConnector.class.getName();

    private Map<String, String> primaryConnectorConfigs = singletonMap(ConnectorConfig.STREAM_CONNECTOR_CLASS, primaryConnectorClass);
    private Map<String, String> secondaryConnectorConfigs = singletonMap(ConnectorConfig.STREAM_CONNECTOR_CLASS, secondaryConnectorClass);

    private IdAndVersionStreamConnectorFactory connectorFactory;

    @Before
    public void setUp() {
        this.connectorFactory = new IdAndVersionStreamConnectorFactory(this.configurationProvider, this.streamConnectorPlugins);
        when(streamConnectorPlugins.newConnector(primaryConnectorClass)).thenReturn(primaryStreamConnector);
        when(streamConnectorPlugins.newConnector(secondaryConnectorClass)).thenReturn(secondaryStreamConnector);

        when(configurationProvider.getPrimaryConnectorConfigs()).thenReturn(primaryConnectorConfigs);
        when(configurationProvider.getSecondaryConnectorConfigs()).thenReturn(secondaryConnectorConfigs);
    }

    @Test
    public void shouldCreateConfiguredStreamConnectors() {
        Pair<IdAndVersionStreamConnector, IdAndVersionStreamConnector> streamConnectors
                = this.connectorFactory.createStreamConnectors();

        assertThat(streamConnectors.getLeft(), is(primaryStreamConnector));
        assertThat(streamConnectors.getRight(), is(secondaryStreamConnector));

        verify(primaryStreamConnector).configure(primaryConnectorConfigs);
        verify(secondaryStreamConnector).configure(secondaryConnectorConfigs);
    }
}