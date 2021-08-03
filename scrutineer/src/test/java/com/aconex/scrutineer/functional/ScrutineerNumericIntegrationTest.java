package com.aconex.scrutineer.functional;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import javax.sql.DataSource;

import com.aconex.scrutineer.Scrutineer;
import com.aconex.scrutineer.elasticsearch.ESIntegrationTestNode;
import com.aconex.scrutineer.elasticsearch.ElasticSearchTestHelper;
import com.aconex.scrutineer.jdbc.HSQLHelper;
import com.google.common.io.ByteStreams;
import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.mockito.MockitoAnnotations;

public class ScrutineerNumericIntegrationTest extends DataSourceBasedDBTestCase {

    private static final String CLUSTER_NAME = "scrutineernumericintegrationtest";
    private HSQLHelper hsqlHelper = new HSQLHelper();
    private Node node;
    private Client client;

    PrintStream printStream = spy(System.err);


    public void testShouldScrutinizeStreamsEffectively() {
        String[] args = {"--jdbcURL", String.format("jdbc:hsqldb:%s", HSQLHelper.INMEM_TEST_DB),
                "--jdbcDriverClass", org.hsqldb.jdbc.JDBCDriver.class.getName(),
                "--jdbcUser", "sa",
                //"--jdbcPassword", "",
                "--clusterName", CLUSTER_NAME,
                "--esHosts", "localhost:9300",
                "--sql", "select id,version from test order by CAST(id AS INTEGER)",
                "--indexName", "test",
                "--numeric"
        };


        System.setErr(printStream);

        Scrutineer.main(args);

        System.err.println(printStream);

        verify(printStream).println("NOTINSECONDARY\t222\t20");
        verify(printStream).println("MISMATCH\t33\t30\tsecondaryVersion=42");
        verify(printStream).println("NOTINPRIMARY\t4\t40");


    }

    @Override
    protected void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        setupHSQLDB();
        setupElasticSearchConnection();
        indexSetupStateForElasticSearch();
        super.setUp();
    }

    private void setupElasticSearchConnection() throws NodeValidationException {
        this.node = ESIntegrationTestNode.elasticSearchTestNode(CLUSTER_NAME);
        this.client = node.client();
    }

    private void setupHSQLDB() throws Exception {
        hsqlHelper = new HSQLHelper();
        hsqlHelper.createHsqldbTables(getDataSet(), getDataSource().getConnection());
    }


    private void indexSetupStateForElasticSearch() throws Exception {
        new ElasticSearchTestHelper(client).deleteIndexIfItExists("test");
        BulkRequestBuilder bulkRequest = client.prepareBulk().setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        URL bulkIndexRequest = this.getClass().getResource("es-numericbulkindex.json");
        byte[] data = ByteStreams.toByteArray(bulkIndexRequest.openStream());
        bulkRequest.add(data, 0, data.length, XContentType.JSON);
        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            throw new RuntimeException("Failed to index data needed for test. " + bulkResponse.buildFailureMessage());
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        hsqlHelper.shutdownHSQL(getDataSource());
        closeElasticSearch();
    }

    private void closeElasticSearch() throws IOException {
        if (client != null) {
            client.close();
        }
        if (node != null) {
            node.close();
        }
    }


    @Override
    protected DataSource getDataSource() {
        return hsqlHelper.setupHSQLDBDataSource();
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("fullnumericintegrationtest.xml");
        return new XmlDataSet(resourceAsStream);
    }
}
