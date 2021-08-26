package com.aconex.scrutineer.functional;

import static org.elasticsearch.common.xcontent.XContentType.JSON;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.TimeZone;

import javax.sql.DataSource;

import com.aconex.scrutineer.elasticsearch.ESIntegrationTestNode;
import com.aconex.scrutineer.elasticsearch.ElasticSearchTestHelper;
import com.aconex.scrutineer.jdbc.HSQLHelper;
import com.aconex.scrutineer.v2.ScrutineerCli2;
import com.google.common.io.ByteStreams;
import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.joda.time.DateTimeZone;
import org.mockito.MockitoAnnotations;

public class ScrutineerCli2IntegrationTest extends DataSourceBasedDBTestCase {
    private static final String CLUSTER1_NAME = "scrutineer2integrationtest1";
    private static final String CLUSTER2_NAME = "scrutineer2integrationtest2";


    private HSQLHelper hsqlHelper = new HSQLHelper();
    private Node node1;
    private Node node2;
    private Client client1;
    private Client client2;

    PrintStream printStream = spy(System.err);

    public void testShouldScrutinizeJdbcAndElasticSearchStreamsEffectively() throws Exception {
        setupElasticSearchConnection1(CLUSTER1_NAME, 9300);
        indexSetupStateForElasticSearchCluster(client1, "test", "es-bulkindex.json");

        TimeZone.setDefault(TimeZone.getTimeZone("GST"));
        DateTimeZone.setDefault(DateTimeZone.forOffsetHours(4));

        String[] args = {
                "--primary-config", "test-jdbc-config.properties",
                "--secondary-config", "test-elasticsearch7-cluster1-config.properties",
                "--versions-as-timestamps"
        };


        System.setErr(printStream);

        ScrutineerCli2.main(args);

        verifyThatErrorsWrittenToStandardError(printStream);
    }


    public void testShouldScrutinizeTwoElasticSearchStreamsEffectively() throws Exception {
        setupElasticSearchConnection1(CLUSTER1_NAME, 9300);
        setupElasticSearchConnection2(CLUSTER2_NAME, 9301);
        indexSetupStateForElasticSearchCluster(client1, "test", "es-bulkindex.json");
        indexSetupStateForElasticSearchCluster(client2, "test2", "es-bulkindex-2.json");

        TimeZone.setDefault(TimeZone.getTimeZone("GST"));
        DateTimeZone.setDefault(DateTimeZone.forOffsetHours(4));


        String[] args = {
                "--primary-config", "test-elasticsearch7-cluster2-config.properties",
                "--secondary-config", "test-elasticsearch7-cluster1-config.properties",
                "--versions-as-timestamps"
        };


        System.setErr(printStream);

        ScrutineerCli2.main(args);

        verifyThatErrorsWrittenToStandardError(printStream);
    }

    protected void verifyThatErrorsWrittenToStandardError(PrintStream printStream) {
        verify(printStream).println("NOTINSECONDARY\t2\t20(1970-01-01T04:00:00.020+04:00)");
        verify(printStream).println("MISMATCH\t3\t30(1970-01-01T04:00:00.030+04:00)\tsecondaryVersion=42(1970-01-01T04:00:00.042+04:00)");
        verify(printStream).println("NOTINPRIMARY\t4\t40(1970-01-01T04:00:00.040+04:00)");
    }

    @Override
    protected void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        setupHSQLDB();
        super.setUp();
    }

    private void setupElasticSearchConnection1(String clusterName, int port) throws NodeValidationException {
        this.node1 = ESIntegrationTestNode.elasticSearchTestNode(clusterName, port);
        this.client1 = node1.client();
    }

    private void setupElasticSearchConnection2(String cluster2Name, int port) throws NodeValidationException {
        this.node2 = ESIntegrationTestNode.elasticSearchTestNode(cluster2Name, port);
        this.client2 = node2.client();
    }

    private void setupHSQLDB() throws Exception {
        hsqlHelper = new HSQLHelper();
        hsqlHelper.createHsqldbTables(getDataSet(), getDataSource().getConnection());
    }


    private void indexSetupStateForElasticSearchCluster(Client client, String indexName, String bulkIndexFileName) throws Exception {
        new ElasticSearchTestHelper(client).deleteIndexIfItExists(indexName);
        BulkRequestBuilder bulkRequest = client.prepareBulk().setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        URL bulkIndexRequest = this.getClass().getResource(bulkIndexFileName);
        byte[] data = ByteStreams.toByteArray(bulkIndexRequest.openStream());
        bulkRequest.add(data, 0, data.length, JSON);
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
        if (client1 != null) {
            client1.close();
        }
        if (node1 != null) {
            node1.close();
        }

        if (client2 != null) {
            client2.close();
        }
        if (node2 != null) {
            node2.close();
        }
    }


    @Override
    protected DataSource getDataSource() {
        return hsqlHelper.setupHSQLDBDataSource();
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("fullintegrationtest.xml");
        return new XmlDataSet(resourceAsStream);

    }

}
