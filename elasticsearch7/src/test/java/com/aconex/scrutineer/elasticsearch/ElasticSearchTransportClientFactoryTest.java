package com.aconex.scrutineer.elasticsearch;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.junit.Before;
import org.junit.Test;

public class ElasticSearchTransportClientFactoryTest {

    private TransportAddress address;
    private ElasticSearchConnectionConfig options = new ElasticSearchConnectionConfig();

    private ElasticSearchTransportClientFactory testInstance;

    @Before
    public void setUp() {
        TransportAddressParser transportAddressParser = new TransportAddressParser();
        this.address = transportAddressParser.convert("127.0.0.1:9300").get(0);

        this.options.setClusterName("mycluster");
        this.options.setEsUsername("user");
        this.options.setEsPassword("secret");
        this.options.setEsSSLVerificationMode("certificate");
        this.options.setEsSSLEnabled(true);
        this.options.setElasticSearchHosts(Arrays.asList(this.address));


        this.testInstance = new ElasticSearchTransportClientFactory();
    }

    @Test
    public void shouldCreateTransportClientSettingsWithCredentials() {
        TransportClient transportClient = testInstance.getTransportClient(options);

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
        this.options.setEsSSLEnabled(false);
        this.options.setEsUsername(null);
        this.options.setEsPassword(null);

        TransportClient transportClient = testInstance.getTransportClient(options);


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
