package com.aconex.scrutineer2.v2;

import com.aconex.scrutineer2.config.CliConfig;
import com.aconex.scrutineer2.elasticsearch.ElasticSearchConnectorConfig;
import com.aconex.scrutineer2.jdbc.JdbcConnectorConfig;
import com.aconex.scrutineer2.v2.configconverter.CliOptionV2ToConfigConverter;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

public class CliOptionV2ToConfigConverterTest {
    private ScrutineerCommandLineOptionsV2 commandLineOptionsV2;
    private CliOptionV2ToConfigConverter converter;

    @Before
    public void setUp() {
        this.commandLineOptionsV2 = new ScrutineerCommandLineOptionsV2();
        commandLineOptionsV2.primaryConfig = "com/aconex/scrutineer2/v2/jdbc.properties";
        commandLineOptionsV2.secondaryConfig = "com/aconex/scrutineer2/v2/elastic-search.properties";

        this.converter = new CliOptionV2ToConfigConverter();
    }

    @Test
    public void getConnectorConfigsShouldLoadPropertiesFromClassPath() {
        CliConfig config = converter.convert(commandLineOptionsV2);

        assertThat(((JdbcConnectorConfig)config.getPrimaryConnectorConfig()).getJdbcUrl(), is("jdbc:hsqldb:mem:test"));
        assertThat(((ElasticSearchConnectorConfig)config.getSecondaryConnectorConfig()).getIndexName(), is("test"));
    }

    @Test
    public void shouldRaiseExceptionWithReasonIfConfigPropertyNotFound() {
        this.commandLineOptionsV2.primaryConfig = "not-found.properties";
        CliConfig cliConfig = converter.convert(this.commandLineOptionsV2);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> cliConfig.getPrimaryConnectorConfig());
        assertThat(ex.getMessage(), is("config property file: 'not-found.properties' not found in classpath, stream config files should be placed under 'config' folder"));
    }
}