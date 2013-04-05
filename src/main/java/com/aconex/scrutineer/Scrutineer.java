package com.aconex.scrutineer;

import com.aconex.scrutineer.elasticsearch.*;
import com.aconex.scrutineer.jdbc.JdbcIdAndVersionStream;
import com.beust.jcommander.JCommander;
import com.fasterxml.sort.DataReaderFactory;
import com.fasterxml.sort.DataWriterFactory;
import com.fasterxml.sort.SortConfig;
import com.fasterxml.sort.Sorter;
import com.fasterxml.sort.util.NaturalComparator;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Scrutineer {

    private static final Logger LOG = LogUtils.loggerForThisClass();

    public static void main(String[] args) {
        DOMConfigurator.configure(Scrutineer.class.getClassLoader().getResource("log4j.xml"));

        Scrutineer scrutineer = new Scrutineer(parseOptions(args));
        execute(scrutineer);
    }

    static void execute(Scrutineer scrutineer) {
        try {
            scrutineer.verify();
        } catch (Exception e) {
            LOG.error("Failure during Scrutineering", e);
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
        idAndVersionFactory = createIdAndVersionFactory();
        ElasticSearchIdAndVersionStream elasticSearchIdAndVersionStream = createElasticSearchIdAndVersionStream(options);
        JdbcIdAndVersionStream jdbcIdAndVersionStream = createJdbcIdAndVersionStream(options);

        verify(elasticSearchIdAndVersionStream, jdbcIdAndVersionStream, new IdAndVersionStreamVerifier());
    }

    void close() {
        closeJdbcConnection();
        closeElasticSearchConnections();
    }

    void closeElasticSearchConnections() {
        if (client != null) {
            client.close();
        }
        if (node != null) {
            node.close();
        }
    }

    void closeJdbcConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void verify(ElasticSearchIdAndVersionStream elasticSearchIdAndVersionStream, JdbcIdAndVersionStream jdbcIdAndVersionStream, IdAndVersionStreamVerifier idAndVersionStreamVerifier) {
        idAndVersionStreamVerifier.verify(jdbcIdAndVersionStream, elasticSearchIdAndVersionStream, new PrintStreamOutputVersionStreamVerifierListener(System.err));
    }


    public Scrutineer(ScrutineerCommandLineOptions options) {
        this.options = options;
    }

    private IdAndVersionFactory createIdAndVersionFactory() {
        return options.numeric ? LongIdAndVersion.FACTORY : StringIdAndVersion.FACTORY;
    }

    ElasticSearchIdAndVersionStream createElasticSearchIdAndVersionStream(ScrutineerCommandLineOptions options) {
        this.node = new NodeFactory().createNode(options);
        this.client = node.client();
        return new ElasticSearchIdAndVersionStream(new ElasticSearchDownloader(client, options.indexName, options.query, idAndVersionFactory), new ElasticSearchSorter(createSorter()), new IteratorFactory(idAndVersionFactory), SystemUtils.getJavaIoTmpDir().getAbsolutePath());
    }

    private Sorter<IdAndVersion> createSorter() {
        SortConfig sortConfig = new SortConfig().withMaxMemoryUsage(DEFAULT_SORT_MEM);
        DataReaderFactory<IdAndVersion> dataReaderFactory = new IdAndVersionDataReaderFactory(idAndVersionFactory);
        DataWriterFactory<IdAndVersion> dataWriterFactory = new IdAndVersionDataWriterFactory();
        return new Sorter<IdAndVersion>(sortConfig, dataReaderFactory, dataWriterFactory, new NaturalComparator<IdAndVersion>());
    }

    JdbcIdAndVersionStream createJdbcIdAndVersionStream(ScrutineerCommandLineOptions options) {
        this.connection = initializeJdbcDriverAndConnection(options);
        return new JdbcIdAndVersionStream(connection, options.sql, idAndVersionFactory);
    }

    private Connection initializeJdbcDriverAndConnection(ScrutineerCommandLineOptions options) {
        try {
            Class.forName(options.jdbcDriverClass).newInstance();
            return DriverManager.getConnection(options.jdbcURL, options.jdbcUser, options.jdbcPassword);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static final int DEFAULT_SORT_MEM = 256 * 1024 * 1024;
    private final ScrutineerCommandLineOptions options;
    private IdAndVersionFactory idAndVersionFactory;
    private Node node;
    private Client client;
    private Connection connection;

}
