package com.aconex.scrutineer.runtime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import com.aconex.scrutineer.IdAndVersionStreamConnector;
import org.junit.Before;
import org.junit.Test;

public class StreamConnectorPluginsTest {
    private StreamConnectorPlugins streamConnectorPlugins;

    @Before
    public void setUp() {
        this.streamConnectorPlugins = new StreamConnectorPlugins();
    }

    @Test
    public void shouldInstantiateARegisterConnector() {
        String connectorClass = "com.aconex.scrutineer.jdbc.JdbcStreamConnector";

        IdAndVersionStreamConnector streamConnector =
                streamConnectorPlugins.newConnector(connectorClass);

        assertThat(streamConnector, is(notNullValue()));
    }

    @Test
    public void shouldThrowExceptionWithAvailableConnectorsWhenClassNotRegistered() {
        String connectorClass = "not-registered";

        try {
            streamConnectorPlugins.newConnector(connectorClass);
            fail();
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage(), is(
                    "Fail to find any connector class that implements IdAndVersionStreamConnector that matches not-registered, available connectors are: com.aconex.scrutineer.jdbc.JdbcStreamConnector,com.aconex.scrutineer.elasticsearch.v7.ElasticSearchStreamConnector"));
        }


    }


}