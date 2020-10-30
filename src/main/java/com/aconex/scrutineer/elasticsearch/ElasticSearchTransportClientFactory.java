package com.aconex.scrutineer.elasticsearch;

import java.util.List;

import com.aconex.scrutineer.LogUtils;
import com.aconex.scrutineer.ScrutineerCommandLineOptions;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.slf4j.Logger;

public class ElasticSearchTransportClientFactory {
    private static final Logger LOG = LogUtils.loggerForThisClass();

    private static final String SOCKET_CONNECT_TIMEOUT = "60s";


    public TransportClient getTransportClient(ScrutineerCommandLineOptions options) {

        if (options.esUsername != null && options.esPassword != null) {
            return createTransportClientWithAuthentication(
                    new Credential(options.esUsername, options.esPassword),
                    options.esSSLVerificationMode,
                    options.esSSLEnabled,
                    options.elasticSearchHosts,
                    options.clusterName);
        } else {
            return createTransportClient(
                    options.elasticSearchHosts,
                    options.esSSLVerificationMode,
                    options.esSSLEnabled,
                    options.clusterName);
        }
    }

    private TransportClient createTransportClientWithAuthentication(
            Credential esCredentials,
            String sslVerificationMode,
            boolean sslEnabled,
            List<TransportAddress> elasticSearchHosts,
            String clusterName) {

        Settings build = createSettings(esCredentials, sslVerificationMode, sslEnabled, clusterName);

        TransportClient transportClient = new PreBuiltXPackTransportClient(build);
        addTransportHosts(transportClient, elasticSearchHosts);
        return transportClient;
    }

    private TransportClient createTransportClient(List<TransportAddress> elasticSearchHosts, String sslVerificationMode, boolean sslEnabled, String clusterName) {
        Settings.Builder builder = settingBuilder(clusterName, sslEnabled, sslVerificationMode);
        TransportClient transportClient = new PreBuiltTransportClient(builder.build());
        addTransportHosts(transportClient, elasticSearchHosts);
        return transportClient;
    }

    private Settings createSettings(Credential esCredentials, String sslVerificationMode, boolean sslEnabled, String clusterName) {
        String clientCredential = esCredentials.getUsername() + ":" + esCredentials.getPassword();

        return settingBuilder(clusterName, sslEnabled, sslVerificationMode)
                .put("xpack.security.user", clientCredential)
                .build();
    }

    private Settings.Builder settingBuilder(String clusterName, boolean sslEnabled, String sslVerificationMode) {
        Settings.Builder settings = Settings.builder();
        if (sslEnabled) {
            settings
                    .put("xpack.security.transport.ssl.enabled", true)
                    .put("xpack.security.transport.ssl.verification_mode", sslVerificationMode);
        }

        return settings
                .put(ClusterName.CLUSTER_NAME_SETTING.getKey(), clusterName)
                .put("transport.tcp.connect_timeout", SOCKET_CONNECT_TIMEOUT);
    }

    private void addTransportHosts(TransportClient transportClient, List<TransportAddress> elasticSearchHosts) {
        LogUtils.debug(LOG, "Connecting to elastic search on hosts: %s ", elasticSearchHosts.toString());

        for (TransportAddress host : elasticSearchHosts) {
            transportClient.addTransportAddress(host);
        }
    }

}
