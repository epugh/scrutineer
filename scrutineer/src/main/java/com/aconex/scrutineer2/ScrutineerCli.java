package com.aconex.scrutineer2;

import com.beust.jcommander.JCommander;
import org.slf4j.Logger;

public class ScrutineerCli {
    private static final Logger LOG = LogUtils.loggerForThisClass();
    private final ScrutineerCommandLineOptions options;

    public ScrutineerCli(ScrutineerCommandLineOptions options) {

        this.options = options;
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
            new ScrutineerCli(options).run();

        } catch (Exception e) {
            LOG.error("Failure during Scrutineering", e);
            System.exit(1);
        }
    }

    private void run() {
        new Scrutineer(new CliOptionToConfigConverter().convert(options), new IdAndVersionStreamVerifier()).verify();
    }
}
