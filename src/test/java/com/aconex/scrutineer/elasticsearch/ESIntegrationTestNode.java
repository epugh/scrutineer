package com.aconex.scrutineer.elasticsearch;

import static java.util.Arrays.asList;

import java.util.Collection;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.InternalSettingsPreparer;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.transport.Netty4Plugin;

public class ESIntegrationTestNode extends Node {
    public static final String CLUSTER_NAME = "scrutineerintegrationtest";

    private ESIntegrationTestNode(Settings preparedSettings, Collection<Class<? extends Plugin>> classpathPlugins) {
        super(InternalSettingsPreparer.prepareEnvironment(preparedSettings, null), classpathPlugins, true);
    }

    @Override
    protected void registerDerivedNodeNameWithLogger(String s) {

    }

    public static Node elasticSearchTestNode() throws NodeValidationException {
        Node node = new ESIntegrationTestNode(
                Settings.builder()
                        .put("cluster.name", CLUSTER_NAME)
                        .put("transport.type", "netty4")
                        .put("http.type", "netty4")
                        .put("http.enabled", "true")
                        .put("path.home", "data")
                        .build(),
                asList(Netty4Plugin.class));
        node.start();
        return node;
    }

}
