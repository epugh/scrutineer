package com.aconex.scrutineer;

import java.util.Map;

import com.aconex.scrutineer.elasticsearch.ElasticSearchStreamConnector;
import com.aconex.scrutineer.jdbc.JdbcStreamConnector;
import com.beust.jcommander.JCommander;
import com.google.common.base.Function;
import org.slf4j.Logger;

public class Scrutineer {

    private static final Logger LOG = LogUtils.loggerForThisClass();

    @SuppressWarnings("PMD.NcssMethodCount")
    public static void main(String[] args) {
        try {
            ScrutineerCommandLineOptions options = new ScrutineerCommandLineOptions();
            JCommander jCommander = new JCommander(options);
            jCommander.parse(args);

            if (options.help) {
                jCommander.usage();
                return;
            }

            execute(new Scrutineer(new ScrutineerCommandLineOptionsExtension(options)));
        } catch (Exception e) {
            LOG.error("Failure during Scrutineering", e);
            System.exit(1);
        }
    }

    static void execute(Scrutineer scrutineer) {
        scrutineer.verify();
    }

    public void verify() {
        Function<Long, Object> formatter = createFormatter();
        IdAndVersionStreamVerifierListener verifierListener = createVerifierListener(formatter);

        this.verify(verifierListener);
    }

    @SuppressWarnings("PMD.NcssMethodCount")
    public void verify(IdAndVersionStreamVerifierListener verifierListener) {
        idAndVersionFactory = createIdAndVersionFactory();

        // secondary
        IdAndVersionStreamConnector elasticSearchStreamConnector = new ElasticSearchStreamConnector();
        IdAndVersionStream elasticSearchIdAndVersionStream =
                createElasticSearchIdAndVersionStream(elasticSearchStreamConnector, optionsExtension.getSecondaryConnectorConfigs());

        // primary
        IdAndVersionStreamConnector jdbcStreamConnector = new JdbcStreamConnector();
        IdAndVersionStream jdbcIdAndVersionStream =
                createJdbcIdAndVersionStream(jdbcStreamConnector, optionsExtension.getPrimaryConnectorConfigs());

        try {
            verify(elasticSearchIdAndVersionStream, jdbcIdAndVersionStream, new IdAndVersionStreamVerifier(), verifierListener);
        } finally {
            close(jdbcStreamConnector, elasticSearchStreamConnector);
        }
    }

    public void close(IdAndVersionStreamConnector elasticSearchStreamConnector, IdAndVersionStreamConnector jdbcStreamConnector) {
        closeJdbcConnection(jdbcStreamConnector);
        closeElasticSearchConnections(elasticSearchStreamConnector);
    }

    void closeElasticSearchConnections(IdAndVersionStreamConnector elasticSearchStreamConnector) {
        elasticSearchStreamConnector.close();
    }

    void closeJdbcConnection(IdAndVersionStreamConnector jdbcStreamConnector) {
        jdbcStreamConnector.close();
    }

    void verify(IdAndVersionStream elasticSearchIdAndVersionStream, IdAndVersionStream jdbcIdAndVersionStream, IdAndVersionStreamVerifier idAndVersionStreamVerifier, IdAndVersionStreamVerifierListener verifierListener) {
        idAndVersionStreamVerifier.verify(jdbcIdAndVersionStream, elasticSearchIdAndVersionStream, verifierListener);
    }

    private Function<Long, Object> createFormatter() {
        Function<Long, Object> formatter = PrintStreamOutputVersionStreamVerifierListener.DEFAULT_FORMATTER;
        if (optionsExtension.versionsAsTimestamps()) {
            formatter = new TimestampFormatter();
        }
        return formatter;
    }

    private IdAndVersionStreamVerifierListener createVerifierListener(Function<Long, Object> formatter) {
        if (optionsExtension.ignoreTimestampsDuringRun()) {
            return createCoincidentPrintStreamListener(formatter);
        } else {
            return createStandardPrintStreamListener(formatter);
        }
    }

    IdAndVersionStreamVerifierListener createStandardPrintStreamListener(Function<Long, Object> formatter) {
        return new PrintStreamOutputVersionStreamVerifierListener(System.err, formatter);
    }

    IdAndVersionStreamVerifierListener createCoincidentPrintStreamListener(Function<Long, Object> formatter) {
        return new CoincidentFilteredStreamVerifierListener(new PrintStreamOutputVersionStreamVerifierListener(System.err, formatter));
    }


    public Scrutineer(ScrutineerCommandLineOptionsExtension options) {
        this.optionsExtension = options;
    }

    private IdAndVersionFactory createIdAndVersionFactory() {
        return optionsExtension.numeric() ? LongIdAndVersion.FACTORY : StringIdAndVersion.FACTORY;
    }

    IdAndVersionStream createElasticSearchIdAndVersionStream(IdAndVersionStreamConnector elasticSearchStreamConnector, Map<String, String> props) {
        elasticSearchStreamConnector.configure(props);
        return elasticSearchStreamConnector.create(idAndVersionFactory);
    }

    IdAndVersionStream createJdbcIdAndVersionStream(IdAndVersionStreamConnector jdbcStreamConnector, Map<String, String> props) {
        jdbcStreamConnector.configure(props);
        return jdbcStreamConnector.create(idAndVersionFactory);
    }

    private final ScrutineerCommandLineOptionsExtension optionsExtension;
    private IdAndVersionFactory idAndVersionFactory;
}
