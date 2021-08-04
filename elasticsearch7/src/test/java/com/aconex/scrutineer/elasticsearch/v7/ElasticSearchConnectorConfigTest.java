package com.aconex.scrutineer.elasticsearch.v7;

import static com.aconex.scrutineer.elasticsearch.v7.ElasticSearchConnectorConfig.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.transport.TransportAddress;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

public class ElasticSearchConnectorConfigTest {
    private ElasticSearchConnectorConfig elasticSearchConnectorConfig;

    private String clusterName = "myCluster";
    private String index = "myIndex";
    private String esQuery = "*";
    private String hosts = "127.0.0.1:9300,127.0.0.2:9301";
    private String username = "user";
    private String password = "secret";
    private String sslEnabled = "false";
    private String sslVerificationMode = "certificate";

    private Map<String, String> props;

    @Before
    public void setUp() throws Exception {
        this.props = new HashMap<>();
        this.props.put(CONFIG_ES_CLUSTER_NAME, clusterName);
        this.props.put(CONFIG_ES_HOSTS, hosts);
        this.props.put(CONFIG_ES_INDEX_NAME, index);
        this.props.put(CONFIG_ES_QUERY, esQuery);
        this.props.put(CONFIG_ES_USERNAME, username);
        this.props.put(CONFIG_ES_PASSWORD, password);
        this.props.put(CONFIG_ES_SSL_VERIFICATION_MODE, sslVerificationMode);
        this.props.put(CONFIG_ES_SSL_ENABLED, sslEnabled);

        this.elasticSearchConnectorConfig = new ElasticSearchConnectorConfig(this.props);
    }

    @Test
    public void shouldGetIndividualEsConfigs() {
        assertThat(this.elasticSearchConnectorConfig.getClusterName(), is(clusterName));
        List<TransportAddress> hostTransportAddresses = this.elasticSearchConnectorConfig.getHosts();
        assertThat(hostTransportAddresses.size(), is(2));
        assertThat(hostTransportAddresses.get(0).getAddress(), Is.is("127.0.0.1"));
        assertThat(hostTransportAddresses.get(0).getPort(), Is.is(9300));
        assertThat(hostTransportAddresses.get(1).getAddress(), Is.is("127.0.0.2"));
        assertThat(hostTransportAddresses.get(1).getPort(), Is.is(9301));
        assertThat(this.elasticSearchConnectorConfig.getIndexName(), is(index));
        assertThat(this.elasticSearchConnectorConfig.getEsQuery(), is(esQuery));
        assertThat(this.elasticSearchConnectorConfig.getEsUsername(), is(username));
        assertThat(this.elasticSearchConnectorConfig.getEsPassword(), is(password));
        assertThat(this.elasticSearchConnectorConfig.getSslVerificationMode(), is(sslVerificationMode));
        assertThat(this.elasticSearchConnectorConfig.isSslEnabled(), is(false));

    }
}