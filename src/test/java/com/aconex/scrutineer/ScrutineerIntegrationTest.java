package com.aconex.scrutineer;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.aconex.scrutineer.elasticsearch.ElasticSearchTestHelper;
import com.aconex.scrutineer.jdbc.HSQLHelper;
import com.google.common.io.ByteStreams;
import com.google.common.io.CountingOutputStream;
import com.google.common.io.NullOutputStream;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.action.bulk.BulkRequestBuilder;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;

public class ScrutineerIntegrationTest extends DataSourceBasedDBTestCase {

    private HSQLHelper hsqlHelper = new HSQLHelper();
    private Node node;
    private Client client;


    public void testShouldScrutinizeStreamsEffectively() {
        String[] args = {"--jdbcURL", String.format("jdbc:hsqldb:%s", HSQLHelper.INMEM_TEST_DB),
                "--jdbcDriverClass", org.hsqldb.jdbc.JDBCDriver.class.getName(),
                "--jdbcUser", "sa",
                //"--jdbcPassword", "",
                "--clusterName", "paul",
                "--sql", "select id,version from test order by id",
                "--indexName", "test"
        };

        Scrutineer.main(args);



    }

    @Override
    protected void setUp() throws Exception {
        setupLogging();
        setupHSQLDB();
        setupElasticSearchConnection();
        indexSetupStateForElasticSearch();
        super.setUp();
    }

    private void setupElasticSearchConnection() {
        // TODO this network crap is annoying..
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "paul").put("network.host", "_en0:ipv4_").build();

        // TODO right now this is just connecting to my local box to see if this actually workes
        //this.node = nodeBuilder().local(true).node();
        this.node = nodeBuilder().settings(settings).client(true).node();

        //this.client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
        this.client = node.client();
    }

    private void setupHSQLDB() throws Exception {
        hsqlHelper = new HSQLHelper();
        hsqlHelper.createHsqldbTables(getDataSet(), getDataSource().getConnection());
    }

    private void setupLogging() {
        BasicConfigurator.configure();
        LogManager.getLoggerRepository().setThreshold(Level.INFO);
    }

    private void indexSetupStateForElasticSearch() throws Exception {
        new ElasticSearchTestHelper(client).deleteIndexIfItExists("test");
        BulkRequest bulkRequest = new BulkRequestBuilder(client).request();
        URL bulkIndexRequest = this.getClass().getResource("es-bulkindex.json");
        bulkRequest.add(ByteStreams.toByteArray(bulkIndexRequest.openStream()), 0, (int) determineTestResourceLength(bulkIndexRequest), true);
        BulkResponse bulkResponse = client.bulk(bulkRequest).actionGet();
        if (bulkResponse.hasFailures()) {
            throw new RuntimeException("Failed to index data needed for test. " + bulkResponse.buildFailureMessage());
        }
    }

    private long determineTestResourceLength(URL bulkIndexRequest) throws IOException {
        CountingOutputStream countingOutputStream = new CountingOutputStream(new NullOutputStream());
        ByteStreams.copy(bulkIndexRequest.openStream(), countingOutputStream);
        return countingOutputStream.getCount();
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
