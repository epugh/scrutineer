package com.aconex.scrutineer2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.beust.jcommander.JCommander;
import org.junit.Test;

public class JcommanderTransportAddressParserTest {

    @Test
    public void shouldCorrectlyParseWithJCommander() {
        String[] args = {"--esHosts", "localhost:9300,127.0.0.1:9301", "--sql", "select * from foo", "--jdbcUser", "sa", "--indexName", "myindex", "--jdbcURL", "blah", "--clusterName", "elasticsearch", "--jdbcDriverClass", "mydriver"};
        ScrutineerCommandLineOptions scrutineerCommandLineOptions = new ScrutineerCommandLineOptions();
        JCommander jCommander = new JCommander(scrutineerCommandLineOptions);
        jCommander.parse(args);
        assertThat(scrutineerCommandLineOptions.elasticSearchHosts.size(), is(2));

        // this is not a bug, the actual Java network code translates localhost =>127.0.0.1
        // this might prove to be a flakey test..
        assertThat(scrutineerCommandLineOptions.elasticSearchHosts.get(0).getAddress(), is("127.0.0.1"));
        assertThat(scrutineerCommandLineOptions.elasticSearchHosts.get(0).getPort(), is(9300));

        assertThat(scrutineerCommandLineOptions.elasticSearchHosts.get(1).getAddress(), is("127.0.0.1"));
        assertThat(scrutineerCommandLineOptions.elasticSearchHosts.get(1).getPort(), is(9301));
    }

}