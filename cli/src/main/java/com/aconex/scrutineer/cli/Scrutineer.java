package com.aconex.scrutineer.cli;

import com.aconex.scrutineer.CoincidentFilteredStreamVerifierListener;
import com.aconex.scrutineer.ConnectorConfig;
import com.aconex.scrutineer.IdAndVersionFactory;
import com.aconex.scrutineer.IdAndVersionStreamConnector;
import com.aconex.scrutineer.IdAndVersionStreamVerifier;
import com.aconex.scrutineer.IdAndVersionStreamVerifierListener;
import com.aconex.scrutineer.LogUtils;
import com.aconex.scrutineer.LongIdAndVersion;
import com.aconex.scrutineer.PrintStreamOutputVersionStreamVerifierListener;
import com.aconex.scrutineer.StringIdAndVersion;
import com.aconex.scrutineer.cli.config.CliConfig;
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
