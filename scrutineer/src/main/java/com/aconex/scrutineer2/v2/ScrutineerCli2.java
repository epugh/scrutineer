package com.aconex.scrutineer2.v2;

import com.aconex.scrutineer2.IdAndVersionStreamVerifier;
import com.aconex.scrutineer2.LogUtils;
import com.aconex.scrutineer2.Scrutineer;
import com.aconex.scrutineer2.config.CliConfig;
import com.aconex.scrutineer2.v2.configconverter.CliOptionV2ToConfigConverter;
import com.beust.jcommander.JCommander;
import org.slf4j.Logger;

public final class ScrutineerCli2 {

    private static final Logger LOG = LogUtils.loggerForThisClass();
    private final ScrutineerCommandLineOptionsV2 optionsV2;

    private ScrutineerCli2(ScrutineerCommandLineOptionsV2 optionsV2) {
        this.optionsV2 = optionsV2;
    }

    @SuppressWarnings("PMD.NcssMethodCount")
    public static void main(String[] args) {
        try {
            ScrutineerCommandLineOptionsV2 optionsV2 = new ScrutineerCommandLineOptionsV2();
            JCommander jCommander = new JCommander(optionsV2);
            jCommander.parse(args);

            if (optionsV2.help) {
                jCommander.usage();
                return;
            }
            new ScrutineerCli2(optionsV2).run();
        } catch (Exception e) {
            LOG.error("Failure during Scrutineering", e);
            System.exit(1);
        }
    }

    private void run() {
        CliConfig config = new CliOptionV2ToConfigConverter().convert(optionsV2);
        Scrutineer scrutineer = new Scrutineer(config, new IdAndVersionStreamVerifier());
        scrutineer.verify();
    }
}
