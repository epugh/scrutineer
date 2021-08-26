package com.aconex.scrutineer.elasticsearch.v7;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.junit.Before;
import org.junit.Test;

public class ElasticSearchTransportClientFactoryTest {

    private TransportAddress address;
    private ElasticSearchStreamConnector.Config config;
    private Map<String, String> props;

    private ElasticSearchTransportClientFactory testInstance;

    @Before
    public void setUp() {
        this.config = new ElasticSearchStreamConnector.Config();
        config.setClusterName("mycluster");
        config.setUsername("user");
        config.setPassword("secret");
        config.setSslVerificationMode("certificate");
        config.setSslEnabled(true);
        config.setHosts(new TransportAddressParser().convert("127.0.0.1:9300"));

        this.address = this.config.getHosts().get(0);
        this.testInstance = new ElasticSearchTransportClientFactory();
    }

    @Test
    public void shouldCreateTransportClientSettingsWithCredentials() {
        TransportClient transportClient = testInstance.getTransportClient(config);

        assertTrue(transportClient instanceof PreBuiltXPackTransportClient);
        List<TransportAddress> transportAddresses = transportClient.transportAddresses();
        assertEquals(1, transportAddresses.size());
        assertEquals(this.address, transportAddresses.iterator().next());

        Settings settings = transportClient.settings();
        assertThat(settings.get("cluster.name"), is("mycluster"));
        assertThat(settings.get("xpack.security.transport.ssl.enabled"), is("true"));
        assertThat(settings.get("xpack.security.transport.ssl.verification_mode"), is("certificate"));
        assertThat(settings.get("xpack.security.user"), is("user:secret"));
        assertThat(settings.get("transport.tcp.connect_timeout"), is("60s"));
    }

    @Test
    public void shouldCreatePreBuiltTransportClientWhenAuthenticationNotRequired() {
        config.setSslEnabled(false);
        config.setUsername(null);
        config.setPassword(null);
        TransportClient transportClient = testInstance.getTransportClient(config);


        assertTrue(transportClient instanceof PreBuiltTransportClient);

        List<TransportAddress> transportAddresses = transportClient.transportAddresses();
        assertEquals(1, transportAddresses.size());
        assertEquals(this.address, transportAddresses.iterator().next());

        Settings settings = transportClient.settings();
        assertThat(settings.get("cluster.name"), is("mycluster"));
        assertThat(settings.get("transport.tcp.connect_timeout"), is("60s"));
        
        assertNull(settings.get("xpack.security.transport.ssl.enabled"));
        assertNull(settings.get("xpack.security.transport.ssl.verification_mode"));
        assertNull(settings.get("xpack.security.user"));
    }
}
