package com.aconex.scrutineer2.elasticsearch.v7;

import com.aconex.scrutineer2.ConnectorConfig;
import com.aconex.scrutineer2.IdAndVersionFactory;
import com.aconex.scrutineer2.IdAndVersionStream;
import com.aconex.scrutineer2.IdAndVersionStreamConnector;
import com.aconex.scrutineer2.javautil.JavaIteratorIdAndVersionStream;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.util.List;

public class ElasticSearchStreamConnector implements IdAndVersionStreamConnector {
    static final int BATCH_SIZE = 10000;
    static final int SCROLL_TIME_IN_MINUTES = 10;

    private Client client;
    private final Config config;
    private final IdAndVersionFactory idAndVersionFactory;

    public ElasticSearchStreamConnector(Config config,IdAndVersionFactory idAndVersionFactory) {
        this.config = config;
        this.idAndVersionFactory = idAndVersionFactory;
    }


    @Override
    public IdAndVersionStream connect() {
        try {
            this.client = new ElasticSearchTransportClientFactory().getTransportClient(this.config);
            SearchResponse initialSearchResponse = startScroll();
            return new JavaIteratorIdAndVersionStream(new IdAndVersionElasticSearchScrollResultIterator(client, initialSearchResponse, idAndVersionFactory));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        closeElasticSearchConnections();
    }

    private void closeElasticSearchConnections() {
        if (client != null) {
            client.close();
        }
    }

    private SearchResponse startScroll() {
        //https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-search-scrolling.html
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(config.getIndexName());

        searchRequestBuilder.addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                .setQuery(createQuery())
                .setSize(BATCH_SIZE)
                .setExplain(false)
                .setFetchSource(false)
                .setVersion(true)
                .setScroll(TimeValue.timeValueMinutes(SCROLL_TIME_IN_MINUTES));

        return searchRequestBuilder.execute().actionGet();
    }
    private QueryStringQueryBuilder createQuery() {
        return QueryBuilders.queryStringQuery(config.query).defaultOperator(Operator.AND).defaultField("*");
    }

    public static class Config implements ConnectorConfig {
        private String clusterName;
        private List<TransportAddress> hosts;
        private String indexName;
        private String query;

        // optional
        private String username;
        private String password;
        private String sslVerificationMode="certificate";
        private boolean sslEnabled=false;

        public String getClusterName() {
            return clusterName;
        }

        public void setClusterName(String clusterName) {
            this.clusterName = clusterName;
        }

        public List<TransportAddress> getHosts() {
            return hosts;
        }

        public void setHosts(String hostsSeparatedByComma) {
            this.hosts = new TransportAddressParser().convert(hostsSeparatedByComma);
        }

        public String getIndexName() {
            return indexName;
        }

        public void setIndexName(String indexName) {
            this.indexName = indexName;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getSslVerificationMode() {
            return sslVerificationMode;
        }

        public void setSslVerificationMode(String sslVerificationMode) {
            this.sslVerificationMode = sslVerificationMode;
        }

        public boolean isSslEnabled() {
            return sslEnabled;
        }

        public void setSslEnabled(boolean sslEnabled) {
            this.sslEnabled = sslEnabled;
        }

        @Override
        public IdAndVersionStreamConnector createConnector(IdAndVersionFactory idAndVersionFactory) {
            return new ElasticSearchStreamConnector(this, idAndVersionFactory);
        }
    }
}
