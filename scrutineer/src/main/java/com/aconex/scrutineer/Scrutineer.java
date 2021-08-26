package com.aconex.scrutineer;

import com.aconex.scrutineer.config.ConfigurationProvider;
import com.aconex.scrutineer.runtime.IdAndVersionStreamConnectorFactory;
import com.aconex.scrutineer.runtime.StreamConnectorPlugins;
import com.beust.jcommander.JCommander;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.io.IOException;

public class Scrutineer {

    private static final Logger LOG = LogUtils.loggerForThisClass();

    public Scrutineer(ConfigurationProvider configurationProvider) {
        this(configurationProvider, new IdAndVersionStreamConnectorFactory(configurationProvider, new StreamConnectorPlugins()));
    }

    public Scrutineer(ConfigurationProvider configurationProvider, IdAndVersionStreamConnectorFactory idAndVersionStreamConnectorFactory) {
        this.optionsExtension = configurationProvider;
        this.idAndVersionStreamConnectorFactory = idAndVersionStreamConnectorFactory;
    }

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

            new Scrutineer(new ScrutineerCommandLineOptionsExtension(options)).verify();
        } catch (Exception e) {
            LOG.error("Failure during Scrutineering", e);
            System.exit(1);
        }
    }

    public void verify() {
        IdAndVersionStreamVerifierListener verifierListener = createVerifierListener();
        this.verify(verifierListener);
    }

    @SuppressWarnings("PMD.NcssMethodCount")
    public void verify(IdAndVersionStreamVerifierListener verifierListener) {
        IdAndVersionFactory idAndVersionFactory = createIdAndVersionFactory();
        Pair<IdAndVersionStreamConnector, IdAndVersionStreamConnector> streamConnectors
                = idAndVersionStreamConnectorFactory.createStreamConnectors();

        try (IdAndVersionStreamConnector primaryStreamConnector = streamConnectors.getLeft();
             IdAndVersionStreamConnector secondaryStreamConnector = streamConnectors.getRight()){
            IdAndVersionStream primaryStream = primaryStreamConnector.connect(idAndVersionFactory);
            IdAndVersionStream secondaryStream = secondaryStreamConnector.connect(idAndVersionFactory);
            verify(secondaryStream, primaryStream, new IdAndVersionStreamVerifier(), verifierListener);
        } catch (IOException e){
            LOG.warn("Failed to close connector", e);
        }
    }

    void verify(IdAndVersionStream secondaryIdAndVersionStream, IdAndVersionStream primaryIdAndVersionStream, IdAndVersionStreamVerifier idAndVersionStreamVerifier, IdAndVersionStreamVerifierListener verifierListener) {
        idAndVersionStreamVerifier.verify(primaryIdAndVersionStream, secondaryIdAndVersionStream, verifierListener);
    }

    private IdAndVersionStreamVerifierListener createVerifierListener() {
        if (optionsExtension.ignoreTimestampsDuringRun()) {
            return createCoincidentPrintStreamListener();
        } else {
            return createStandardPrintStreamListener();
        }
    }

    IdAndVersionStreamVerifierListener createStandardPrintStreamListener() {
        return new PrintStreamOutputVersionStreamVerifierListener(System.err, optionsExtension.versionsAsTimestamps());
    }

    IdAndVersionStreamVerifierListener createCoincidentPrintStreamListener() {
        return new CoincidentFilteredStreamVerifierListener(new PrintStreamOutputVersionStreamVerifierListener(System.err, optionsExtension.versionsAsTimestamps()));
    }

    private IdAndVersionFactory createIdAndVersionFactory() {
        return optionsExtension.numeric() ? LongIdAndVersion.FACTORY : StringIdAndVersion.FACTORY;
    }

    private final ConfigurationProvider optionsExtension;
    private final IdAndVersionStreamConnectorFactory idAndVersionStreamConnectorFactory;
}
