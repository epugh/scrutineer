package com.aconex.scrutineer;


import org.elasticsearch.common.settings.Settings;


public class NodeFactory {


    Settings createSettings(ScrutineerCommandLineOptions commandLineOptions) {
        return Settings.builder()
                .put("cluster.name", commandLineOptions.clusterName)
                .put("node.client", true)
                .put("node.name", "scrutineer")
                //.put("es.",System.getProperties())
                .build();
    }

}
