package com.aconex.scrutineer.elasticsearch7;


import com.aconex.scrutineer.IdAndVersion;
import com.aconex.scrutineer.IdAndVersionFactory;
import com.aconex.scrutineer.StringIdAndVersion;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class ElasticSearchIdAndVersionStreamIntegrationTest {

    private static final String INDEX_NAME = "local";
	private final IdAndVersionFactory idAndVersionFactory = StringIdAndVersion.FACTORY;
    private Client client;

    @Before
    public void setup() throws NodeValidationException {
        Node node = ESIntegrationTestNode.elasticSearchTestNode();
        client = node.client();
        deleteIndexIfExists();

        indexIdAndVersion("1", 1);
        indexIdAndVersion("3", 3);
        indexIdAndVersion("2", 2);

        // make explicit _refresh call to make documents visible
        client.admin().indices().prepareRefresh(INDEX_NAME).execute().actionGet();
    }

    @After
    public void teardown() {
        client.close();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldGetStreamFromElasticSearch() {
        ElasticSearchConnectorConfig config = new ElasticSearchConnectorConfig();
        config.setHosts("127.0.0.1:9300");
        config.setClusterName(ESIntegrationTestNode.CLUSTER_NAME);
        config.setIndexName(INDEX_NAME);
        config.setQuery("*");

        try(ElasticSearchStreamConnector connector = new ElasticSearchStreamConnector(config, idAndVersionFactory)) {
            connector.open();
            Iterator<IdAndVersion> iterator = connector.stream();
            List<IdAndVersion> results = new ArrayList<>();
            iterator.forEachRemaining(results::add);

            assertThat(results, containsInAnyOrder(
                    new StringIdAndVersion("1", 1),
                    new StringIdAndVersion("2", 2),
                    new StringIdAndVersion("3", 3)
            ));
        }
    }

    private void deleteIndexIfExists() {
        ElasticSearchTestHelper elasticSearchTestHelper = new ElasticSearchTestHelper(client);
        elasticSearchTestHelper.deleteIndexIfItExists(INDEX_NAME);
    }

    private void indexIdAndVersion(String id, long version) {
        client.prepareIndex().setIndex(INDEX_NAME).setId(id).setVersion(version).setVersionType(VersionType.EXTERNAL).setSource("value", 1).execute().actionGet();
    }

}
