package com.aconex.scrutineer;

import com.aconex.scrutineer.config.ConfigurationProvider;
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

        IdAndVersionStreamConnector secondaryStreamConnector = streamConnectors.getRight();
        IdAndVersionStream secondaryStream = secondaryStreamConnector.create(idAndVersionFactory);

        IdAndVersionStreamConnector primaryStreamConnector = streamConnectors.getLeft();
        IdAndVersionStream primaryStream = primaryStreamConnector.create(idAndVersionFactory);

        try {
            verify(secondaryStream, primaryStream, new IdAndVersionStreamVerifier(), verifierListener);
        } finally {
            close(primaryStreamConnector, secondaryStreamConnector);
        }
    }

    void close(IdAndVersionStreamConnector secondaryStreamConnector, IdAndVersionStreamConnector primaryStreamConnector) {
        primaryStreamConnector.close();
        secondaryStreamConnector.close();
    }

    void verify(IdAndVersionStream secondaryIdAndVersionStream, IdAndVersionStream primaryIdAndVersionStream, IdAndVersionStreamVerifier idAndVersionStreamVerifier, IdAndVersionStreamVerifierListener verifierListener) {
        idAndVersionStreamVerifier.verify(primaryIdAndVersionStream, secondaryIdAndVersionStream, verifierListener);
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

    public Scrutineer(ConfigurationProvider configurationProvider) {
        this(configurationProvider, new IdAndVersionStreamConnectorFactory(configurationProvider, new StreamConnectorPlugins()));
    }

    public Scrutineer(ConfigurationProvider configurationProvider, IdAndVersionStreamConnectorFactory idAndVersionStreamConnectorFactory) {
        this.optionsExtension = configurationProvider;
        this.idAndVersionStreamConnectorFactory = idAndVersionStreamConnectorFactory;
    }

    private IdAndVersionFactory createIdAndVersionFactory() {
        return optionsExtension.numeric() ? LongIdAndVersion.FACTORY : StringIdAndVersion.FACTORY;
    }

    private final ConfigurationProvider optionsExtension;
    private final IdAndVersionStreamConnectorFactory idAndVersionStreamConnectorFactory;
}
