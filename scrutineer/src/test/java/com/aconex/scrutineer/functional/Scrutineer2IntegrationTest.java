package com.aconex.scrutineer.functional;

import java.util.TimeZone;

import com.aconex.scrutineer.v2.Scrutineer2;
import org.joda.time.DateTimeZone;

public class Scrutineer2IntegrationTest extends ScrutineerIntegrationTest {

    @Override
    public void testShouldScrutinizeStreamsEffectively() {

        TimeZone.setDefault(TimeZone.getTimeZone("GST"));
        DateTimeZone.setDefault(DateTimeZone.forOffsetHours(4));

        String[] args = {
                "--primary-config", "test-jdbc-config.properties",
                "--secondary-config", "test-elasticsearch7-config.properties",
                "--versions-as-timestamps"
        };


        System.setErr(printStream);

        Scrutineer2.main(args);

        verifyThatErrorsWrittenToStandardError(printStream);
    }
}
