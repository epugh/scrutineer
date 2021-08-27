package com.aconex.scrutineer2.functional;

import com.aconex.scrutineer2.ScrutineerCli;
import com.aconex.scrutineer2.elasticsearch.ESIntegrationTestNode;
import com.aconex.scrutineer2.elasticsearch.ElasticSearchTestHelper;
import com.aconex.scrutineer2.jdbc.HSQLHelper;
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

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.TimeZone;

import static org.elasticsearch.common.xcontent.XContentType.JSON;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class ScrutineerIntegrationTest extends DataSourceBasedDBTestCase {


    private HSQLHelper hsqlHelper = new HSQLHelper();
    private Node node;
    private Client client;


    PrintStream printStream = spy(System.err);


    public void testShouldScrutinizeStreamsEffectively() {

        TimeZone.setDefault(TimeZone.getTimeZone("GST"));
        DateTimeZone.setDefault(DateTimeZone.forOffsetHours(4));

        String[] args = {"--jdbcURL", String.format("jdbc:hsqldb:%s", HSQLHelper.INMEM_TEST_DB),
                "--jdbcDriverClass", org.hsqldb.jdbc.JDBCDriver.class.getName(),
                "--jdbcUser", "sa",
                //"--jdbcPassword", "",
                "--clusterName", ESIntegrationTestNode.CLUSTER_NAME,
                "--esHosts", "localhost:9300",
                "--sql", "select id,version from test order by id",
                "--indexName", "test",
                "--versions-as-timestamps"
        };


        System.setErr(printStream);

        ScrutineerCli.main(args);

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
        setupElasticSearchConnection();
        indexSetupStateForElasticSearch();
        super.setUp();
    }

    private void setupElasticSearchConnection() throws NodeValidationException {
        this.node = ESIntegrationTestNode.elasticSearchTestNode();
        this.client = node.client();
    }

    private void setupHSQLDB() throws Exception {
        hsqlHelper = new HSQLHelper();
        hsqlHelper.createHsqldbTables(getDataSet(), getDataSource().getConnection());
    }


    private void indexSetupStateForElasticSearch() throws Exception {
        new ElasticSearchTestHelper(client).deleteIndexIfItExists("test");
        BulkRequestBuilder bulkRequest = client.prepareBulk().setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        URL bulkIndexRequest = this.getClass().getResource("es-bulkindex.json");
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
