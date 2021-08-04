package com.aconex.scrutineer;

import com.aconex.scrutineer.runtime.IdAndVersionStreamConnectorFactory;
import com.aconex.scrutineer.runtime.StreamConnectorPlugins;
import com.beust.jcommander.JCommander;
import com.google.common.base.Function;
import org.apache.commons.lang3.tuple.Pair;
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
        IdAndVersionFactory idAndVersionFactory = createIdAndVersionFactory();

        Pair<IdAndVersionStreamConnector, IdAndVersionStreamConnector> streamConnectors
                = idAndVersionStreamConnectorFactory.createStreamConnectors();

        // secondary
        IdAndVersionStreamConnector elasticSearchStreamConnector = streamConnectors.getRight();
        IdAndVersionStream elasticSearchIdAndVersionStream = elasticSearchStreamConnector.create(idAndVersionFactory);

        // primary
        IdAndVersionStreamConnector jdbcStreamConnector = streamConnectors.getLeft();
        IdAndVersionStream jdbcIdAndVersionStream = jdbcStreamConnector.create(idAndVersionFactory);

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
        this(options, new IdAndVersionStreamConnectorFactory(options, new StreamConnectorPlugins()));
    }

    public Scrutineer(ScrutineerCommandLineOptionsExtension options, IdAndVersionStreamConnectorFactory idAndVersionStreamConnectorFactory) {
        this.optionsExtension = options;
        this.idAndVersionStreamConnectorFactory = idAndVersionStreamConnectorFactory;
    }

    private IdAndVersionFactory createIdAndVersionFactory() {
        return optionsExtension.numeric() ? LongIdAndVersion.FACTORY : StringIdAndVersion.FACTORY;
    }

    private final ScrutineerCommandLineOptionsExtension optionsExtension;
    private final IdAndVersionStreamConnectorFactory idAndVersionStreamConnectorFactory;
}
