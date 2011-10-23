package com.aconex.scrutineer;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.aconex.scrutineer.elasticsearch.ElasticSearchDownloader;
import com.aconex.scrutineer.elasticsearch.ElasticSearchIdAndVersionStream;
import com.aconex.scrutineer.jdbc.JdbcIdAndVersionStream;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.apache.log4j.BasicConfigurator;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;

public class Scrutineer {


    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        Scrutineer scrutineer = new Scrutineer(parseOptions(args));
        try {
            scrutineer.verify();
        } finally {
            scrutineer.close();
        }
    }

    private static ScrutineerCommandLineOptions parseOptions(String[] args) {
        ScrutineerCommandLineOptions options = new ScrutineerCommandLineOptions();
        new JCommander(options, args);
        return options;
    }

    public void verify() {
        ElasticSearchIdAndVersionStream elasticSearchIdAndVersionStream = createElasticSearchIdAndVersionStream(options);
        JdbcIdAndVersionStream jdbcIdAndVersionStream = createJdbcIdAndVersionStream(options);

        verify(elasticSearchIdAndVersionStream, jdbcIdAndVersionStream, new IdAndVersionStreamVerifier());
    }

    private void close() {
        if (client != null) {
            client.close();
        }
        if (node != null) {
            node.close();
        }
    }

    private void verify(ElasticSearchIdAndVersionStream elasticSearchIdAndVersionStream, JdbcIdAndVersionStream jdbcIdAndVersionStream, IdAndVersionStreamVerifier idAndVersionStreamVerifier) {
        idAndVersionStreamVerifier.verify(jdbcIdAndVersionStream, elasticSearchIdAndVersionStream, new PrintStreamOutputVersionStreamVerifierListener(System.err));
    }


    public Scrutineer(ScrutineerCommandLineOptions options) {
        this.options = options;
    }


    private ElasticSearchIdAndVersionStream createElasticSearchIdAndVersionStream(ScrutineerCommandLineOptions options) {
        this.node = nodeBuilder().client(true).clusterName(options.clusterName).node();
        this.client = node.client();
        return ElasticSearchIdAndVersionStream.withDefaults(new ElasticSearchDownloader(client, options.indexName));
    }

    private JdbcIdAndVersionStream createJdbcIdAndVersionStream(ScrutineerCommandLineOptions options) {
        return new JdbcIdAndVersionStream(createDataSource(initializeJdbcDriverAndConnection(options)), options.sql);
    }

    private static Connection initializeJdbcDriverAndConnection(ScrutineerCommandLineOptions options) {
        try {
            Class.forName(options.jdbcDriverClass).newInstance();
            return DriverManager.getConnection(options.jdbcURL, options.jdbcUser, options.jdbcPassword);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static DataSource createDataSource(final Connection connection) {
        return new DataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                return connection;
            }

            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                throw new UnsupportedOperationException("This method is not supported");
            }

            @Override
            public PrintWriter getLogWriter() throws SQLException {
                return null;
            }

            @Override
            public void setLogWriter(PrintWriter out) throws SQLException {

            }

            @Override
            public void setLoginTimeout(int seconds) throws SQLException {

            }

            @Override
            public int getLoginTimeout() throws SQLException {
                return 0;
            }

            @Override
            public <T> T unwrap(Class<T> iface) throws SQLException {
                return null;
            }

            @Override
            public boolean isWrapperFor(Class<?> iface) throws SQLException {
                return false;
            }
        };
    }

    private final ScrutineerCommandLineOptions options;
    private Node node;
    private Client client;

    @Parameters(separators = "=")
    public static class ScrutineerCommandLineOptions {
        @Parameter(names = "--clusterName", description = "ElasticSearch cluster name identifier", required = true)
        public String clusterName;

        @Parameter(names = "--indexName", description = "ElasticSearch index name to Verify", required = true)
        public String indexName;

        @Parameter(names = "--jdbcDriverClass", description = "FQN of the JDBC Driver class", required = true)
        public String jdbcDriverClass;

        @Parameter(names = "--jdbcURL", description = "JDBC URL of the Connection of the Primary source", required = true)
        public String jdbcURL;

        @Parameter(names = "--jdbcUser", description = "JDBC Username", required = true)
        public String jdbcUser;

        @Parameter(names = "--jdbcPassword", description = "JDBC Password", required = true)
        public String jdbcPassword;

        @Parameter(names = "--sql", description = "SQL used to create Primary stream, which should return results in _lexicographical_ order", required = true)
        public String sql;
    }
}
