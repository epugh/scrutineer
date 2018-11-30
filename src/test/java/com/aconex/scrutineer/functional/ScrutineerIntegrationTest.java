package com.aconex.scrutineer.functional;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import static org.mockito.Mockito.verify;

import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.TimeZone;
import javax.sql.DataSource;

import com.aconex.scrutineer.Scrutineer;
import com.aconex.scrutineer.elasticsearch.ElasticSearchTestHelper;
import com.aconex.scrutineer.jdbc.HSQLHelper;
import com.google.common.io.ByteStreams;
import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.joda.time.DateTimeZone;
import org.elasticsearch.node.Node;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ScrutineerIntegrationTest extends DataSourceBasedDBTestCase {

    private static final String CLUSTER_NAME = "scrutineerintegrationtest";
    private HSQLHelper hsqlHelper = new HSQLHelper();
    private Node node;
    private Client client;

    @Mock
    PrintStream printStream;


    public void testShouldScrutinizeStreamsEffectively() {

        TimeZone.setDefault(TimeZone.getTimeZone("GST"));
        DateTimeZone.setDefault(DateTimeZone.forOffsetHours(4));

        String[] args = {"--jdbcURL", String.format("jdbc:hsqldb:%s", HSQLHelper.INMEM_TEST_DB),
                "--jdbcDriverClass", org.hsqldb.jdbc.JDBCDriver.class.getName(),
                "--jdbcUser", "sa",
                //"--jdbcPassword", "",
                "--clusterName", CLUSTER_NAME,
                "--sql", "select id,version from test order by id",
                "--indexName", "test",
                "--versions-as-timestamps"
        };


        System.setErr(printStream);

        Scrutineer.main(args);

        verify(printStream).println("NOTINSECONDARY\t2\t20(1970-01-01T04:00:00.020+04:00)");
        verify(printStream).println("MISMATCH\t3\t30(1970-01-01T04:00:00.030+04:00)\tsecondaryVersion=42(1970-01-01T04:00:00.042+04:00)");
        verify(printStream).println("NOTINPRIMARY\t4\t40(1970-01-01T04:00:00.040+04:00)");


    }

    @Override
    protected void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        setupHSQLDB();
        setupElasticSearchConnection();
        indexSetupStateForElasticSearch();
        super.setUp();
    }

    private void setupElasticSearchConnection() {
        this.node = nodeBuilder().clusterName(CLUSTER_NAME).node();
        this.client = node.client();
    }

    private void setupHSQLDB() throws Exception {
        hsqlHelper = new HSQLHelper();
        hsqlHelper.createHsqldbTables(getDataSet(), getDataSource().getConnection());
    }


    private void indexSetupStateForElasticSearch() throws Exception {
        new ElasticSearchTestHelper(client).deleteIndexIfItExists("test");
        BulkRequest bulkRequest = new BulkRequestBuilder(client).request();
        URL bulkIndexRequest = this.getClass().getResource("es-bulkindex.json");
        byte[] data = ByteStreams.toByteArray(bulkIndexRequest.openStream());
        bulkRequest.add(data, 0, data.length);
        BulkResponse bulkResponse = client.bulk(bulkRequest).actionGet();
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

    private void closeElasticSearch() {
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
        InputStream resourceAsStream = this.getClass().getResourceAsStream("fullintegrationtest.xml");
        return new XmlDataSet(resourceAsStream);

    }
}
