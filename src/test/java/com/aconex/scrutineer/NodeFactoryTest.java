package com.aconex.scrutineer;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.elasticsearch.common.settings.Settings;
import org.junit.Test;

public class NodeFactoryTest {

    @Test
    public void shouldCreateNodeWithCorrectSettings() {

        NodeFactory nodeFactory = new NodeFactory();
        ScrutineerCommandLineOptions commandLineOptions = new ScrutineerCommandLineOptions();
        commandLineOptions.clusterName = "mycluster";

        Settings settings = nodeFactory.createSettings(commandLineOptions);

        assertThat(settings.get("cluster.name"), is("mycluster"));
        assertThat(settings.get("node.name"), is("scrutineer"));
    }

}
