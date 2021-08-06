package com.aconex.scrutineer.v2;

import com.aconex.scrutineer.LogUtils;
import com.aconex.scrutineer.Scrutineer;
import com.beust.jcommander.JCommander;
import org.slf4j.Logger;

public final class Scrutineer2 {

    private static final Logger LOG = LogUtils.loggerForThisClass();

    private Scrutineer2() {
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

            Scrutineer scrutineer = new Scrutineer(new ScrutineerCommandLineOptionsV2Extension(optionsV2));
            scrutineer.verify();
        } catch (Exception e) {
            LOG.error("Failure during Scrutineering", e);
            System.exit(1);
        }
    }

}
