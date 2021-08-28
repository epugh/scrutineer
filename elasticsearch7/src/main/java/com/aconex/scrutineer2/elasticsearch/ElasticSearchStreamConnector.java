package com.aconex.scrutineer2.elasticsearch;

import com.aconex.scrutineer2.IdAndVersionFactory;
import com.aconex.scrutineer2.IdAndVersionStream;
import com.aconex.scrutineer2.IdAndVersionStreamConnector;
import com.aconex.scrutineer2.javautil.JavaIteratorIdAndVersionStream;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

public class ElasticSearchStreamConnector implements IdAndVersionStreamConnector {
    static final int BATCH_SIZE = 10000;
    static final int SCROLL_TIME_IN_MINUTES = 10;

    private Client client;
    private final ElasticSearchConnectorConfig config;
    private final IdAndVersionFactory idAndVersionFactory;

    public ElasticSearchStreamConnector(ElasticSearchConnectorConfig config, IdAndVersionFactory idAndVersionFactory) {
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
        return QueryBuilders.queryStringQuery(config.getQuery()).defaultOperator(Operator.AND).defaultField("*");
    }


}
