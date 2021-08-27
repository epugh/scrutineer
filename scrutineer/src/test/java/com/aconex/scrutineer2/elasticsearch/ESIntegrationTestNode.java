package com.aconex.scrutineer2.elasticsearch;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.Collections;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.InternalSettingsPreparer;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.transport.Netty4Plugin;

public class ESIntegrationTestNode extends Node {
    public static final String CLUSTER_NAME = "scrutineerintegrationtest";
    public static final String TEST_NODE_NAME = "Test Node";

    private ESIntegrationTestNode(Settings preparedSettings, Collection<Class<? extends Plugin>> classpathPlugins) {
        super(InternalSettingsPreparer.prepareEnvironment(preparedSettings, Collections.emptyMap(), null, () -> TEST_NODE_NAME), classpathPlugins, true);
    }

    public static Node elasticSearchTestNode() throws NodeValidationException {
        return elasticSearchTestNode(CLUSTER_NAME, 9300);
    }

    public static Node elasticSearchTestNode(String clusterName) throws NodeValidationException {
        return elasticSearchTestNode(clusterName, 9300);
    }

    public static Node elasticSearchTestNode(String clusterName, int tcpPort) throws NodeValidationException {
        Node node = new ESIntegrationTestNode(
                Settings.builder()
                        .put("cluster.name", clusterName)
                        .put("transport.type", "netty4")
                        .put("transport.tcp.port", tcpPort + "")
                        .put("http.type", "netty4")
                        .put("path.home", "data/" + clusterName)
                        .put("node.name", TEST_NODE_NAME)
                        .build(),
                asList(Netty4Plugin.class));
        node.start();
        return node;
    }

}
