package com.aconex.scrutineer;

import com.aconex.scrutineer.config.CliConfig;
import org.slf4j.Logger;

import java.io.IOException;

public class Scrutineer {
    private static final Logger LOG = LogUtils.loggerForThisClass();
    private final CliConfig cliConfig;
    private final IdAndVersionStreamVerifier idAndVersionStreamVerifier;

    public Scrutineer(CliConfig cliConfig, IdAndVersionStreamVerifier idAndVersionStreamVerifier) {
        this.cliConfig = cliConfig;
        this.idAndVersionStreamVerifier = idAndVersionStreamVerifier;
    }

    @SuppressWarnings("PMD.NcssMethodCount")
    public void verify() {
        IdAndVersionStreamVerifierListener verifierListener = createVerifierListener();

        try (IdAndVersionStreamConnector primaryStreamConnector = createConnector(cliConfig.getPrimaryConnectorConfig());
             IdAndVersionStreamConnector secondaryStreamConnector = createConnector(cliConfig.getSecondaryConnectorConfig())){
            idAndVersionStreamVerifier.verify(primaryStreamConnector, secondaryStreamConnector, verifierListener);
        } catch (IOException e){
            LOG.warn("Failed to close connector", e);
        }
    }

    IdAndVersionStreamConnector createConnector(ConnectorConfig connectorConfig) {
        IdAndVersionFactory idAndVersionFactory = createIdAndVersionFactory();
        return connectorConfig.createConnector(idAndVersionFactory);
    }

    IdAndVersionStreamVerifierListener createVerifierListener() {
        if (cliConfig.ignoreTimestampsDuringRun()) {
            return createCoincidentPrintStreamListener();
        } else {
            return createStandardPrintStreamListener();
        }
    }

    private IdAndVersionStreamVerifierListener createStandardPrintStreamListener() {
        return new PrintStreamOutputVersionStreamVerifierListener(System.err, cliConfig.versionsAsTimestamps());
    }

    private IdAndVersionStreamVerifierListener createCoincidentPrintStreamListener() {
        return new CoincidentFilteredStreamVerifierListener(new PrintStreamOutputVersionStreamVerifierListener(System.err, cliConfig.versionsAsTimestamps()));
    }

    private IdAndVersionFactory createIdAndVersionFactory() {
        return cliConfig.numeric() ? LongIdAndVersion.FACTORY : StringIdAndVersion.FACTORY;
    }

}
