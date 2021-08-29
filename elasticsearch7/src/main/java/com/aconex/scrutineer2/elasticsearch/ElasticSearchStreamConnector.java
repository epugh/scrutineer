package com.aconex.scrutineer2.elasticsearch;

import com.aconex.scrutineer2.IdAndVersion;
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

import java.util.Arrays;
import java.util.Iterator;

public class ElasticSearchStreamConnector implements IdAndVersionStreamConnector {
    private Client client;
    private final ElasticSearchConnectorConfig config;
    private final IdAndVersionFactory idAndVersionFactory;
    private String scrollId;


    public ElasticSearchStreamConnector(ElasticSearchConnectorConfig config, IdAndVersionFactory idAndVersionFactory) {
        this.config = config;
        this.idAndVersionFactory = idAndVersionFactory;
    }

    @Override
    public IdAndVersionStream connect() {
        this.client = new ElasticSearchTransportClientFactory().getTransportClient(this.config);
        SearchResponse initialSearchResponse = startScroll();
        scrollId = initialSearchResponse.getScrollId();
        return createStream(initialSearchResponse);
    }

    private JavaIteratorIdAndVersionStream createStream(SearchResponse initialSearchResponse) {
        return new JavaIteratorIdAndVersionStream(
                new IdAndVersionBatchResultIterator(this::scroll, extractHits(initialSearchResponse))
        );
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
                .setSize(config.getBatchSize())
                .setExplain(false)
                .setFetchSource(false)
                .setVersion(true)
                .setScroll(TimeValue.timeValueMinutes(config.getScrollTimeInMinutes()));

        return searchRequestBuilder.execute().actionGet();
    }
    private QueryStringQueryBuilder createQuery() {
        return QueryBuilders.queryStringQuery(config.getQuery()).defaultOperator(Operator.AND).defaultField("*");
    }
    private Iterator<IdAndVersion> scroll() {
        return extractHits(client.prepareSearchScroll(scrollId)
                .setScroll(TimeValue.timeValueMinutes(config.getScrollTimeInMinutes()))
                .execute()
                .actionGet());
    }
    private Iterator<IdAndVersion> extractHits(SearchResponse searchResponse) {
        return Arrays.stream(searchResponse.getHits().getHits()).map(hit-> idAndVersionFactory.create(hit.getId(), hit.getVersion())).iterator();
    }
}
