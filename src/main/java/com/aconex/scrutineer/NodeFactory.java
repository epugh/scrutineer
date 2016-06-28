package com.aconex.scrutineer;


import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

public class NodeFactory {

    public Node createNode(ScrutineerCommandLineOptions commandLineOptions) {
        return nodeBuilder().settings(createSettings(commandLineOptions)).node();
    }

    Settings createSettings(ScrutineerCommandLineOptions commandLineOptions) {
        return ImmutableSettings.settingsBuilder()
                .put("cluster.name", commandLineOptions.clusterName)
                .put("node.client", true)
                .put("node.name", "scrutineer")
                .putProperties("es.",System.getProperties())
                .build();
    }

}
