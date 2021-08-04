package com.aconex.scrutineer.v2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class ScrutineerCommandLineOptionsV2ExtensionTest {
    private ScrutineerCommandLineOptionsV2 commandLineOptionsV2;
    private ScrutineerCommandLineOptionsV2Extension commandLineOptionsV2Extension;

    @Before
    public void setUp() {
        this.commandLineOptionsV2 = new ScrutineerCommandLineOptionsV2();
        commandLineOptionsV2.primaryConfig = "com/aconex/scrutineer/v2/jdbc.properties";
        commandLineOptionsV2.secondaryConfig = "com/aconex/scrutineer/v2/opensearch.properties";

        this.commandLineOptionsV2Extension = new ScrutineerCommandLineOptionsV2Extension(commandLineOptionsV2);
    }

    @Test
    public void getConnectorConfigsShouldLoadPropertiesFromClassPath() {
        Map<String, String> primaryConnectorConfigs = commandLineOptionsV2Extension.getPrimaryConnectorConfigs();
        assertThat(primaryConnectorConfigs.get("stream.connector.class"), is("com.aconex.scrutineer.jdbc.JdbcStreamConnector"));
        assertThat(primaryConnectorConfigs.get("jdbc.url"), is("jdbc:hsqldb:mem:test"));

        Map<String, String> secondaryConnectorConfigs = commandLineOptionsV2Extension.getSecondaryConnectorConfigs();
        assertThat(secondaryConnectorConfigs.get("stream.connector.class"), is("com.aconex.scrutineer.opensearch.OpenSearchStreamConnector"));
        assertThat(secondaryConnectorConfigs.get("connection.url"), is("http://localhost:9200"));
    }

    @Test
    public void shouldRaiseExceptionWithReasonIfConfigPropertyNotFound() {
        this.commandLineOptionsV2.primaryConfig = "not-found.properties";
        this.commandLineOptionsV2Extension = new ScrutineerCommandLineOptionsV2Extension(commandLineOptionsV2);

        try {
            commandLineOptionsV2Extension.getPrimaryConnectorConfigs();
            fail();
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage(), is("config property file: 'not-found.properties' not found in classpath, stream config files should be placed under 'config' folder"));
        }

    }
}