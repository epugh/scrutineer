package com.aconex.scrutineer2.elasticsearch;

import com.aconex.scrutineer2.AbstractIdAndVersionStreamConnector;
import com.aconex.scrutineer2.ConnectorConfig;
import com.aconex.scrutineer2.IdAndVersion;
import com.aconex.scrutineer2.IdAndVersionFactory;
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

public class ElasticSearchStreamConnector extends AbstractIdAndVersionStreamConnector {
    private Client client;
    private String scrollId;
    private SearchResponse initialSearchResponse;

    protected ElasticSearchStreamConnector(ConnectorConfig connectorConfig, IdAndVersionFactory idAndVersionFactory) {
        super(connectorConfig, idAndVersionFactory);
    }

    @Override
    public void open() {
        this.client = new ElasticSearchTransportClientFactory().getTransportClient(this.getConfig());
        initialSearchResponse = startScroll();
        scrollId = initialSearchResponse.getScrollId();
    }

    public Iterator<IdAndVersion> fetchFromSource() {
        return new IdAndVersionBatchResultIterator(this::scroll, extractHits(initialSearchResponse));
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
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(getConfig().getIndexName());

        searchRequestBuilder.addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                .setQuery(createQuery())
                .setSize(getConfig().getBatchSize())
                .setExplain(false)
                .setFetchSource(false)
                .setVersion(true)
                .setScroll(TimeValue.timeValueMinutes(getConfig().getScrollTimeInMinutes()));

        return searchRequestBuilder.execute().actionGet();
    }
    private QueryStringQueryBuilder createQuery() {
        return QueryBuilders.queryStringQuery(getConfig().getQuery()).defaultOperator(Operator.AND).defaultField("*");
    }
    private Iterator<IdAndVersion> scroll() {
        return extractHits(client.prepareSearchScroll(scrollId)
                .setScroll(TimeValue.timeValueMinutes(getConfig().getScrollTimeInMinutes()))
                .execute()
                .actionGet());
    }
    private Iterator<IdAndVersion> extractHits(SearchResponse searchResponse) {
        return Arrays.stream(searchResponse.getHits().getHits()).map(hit-> getIdAndVersionFactory().create(hit.getId(), hit.getVersion())).iterator();
    }
    private ElasticSearchConnectorConfig getConfig(){
        return (ElasticSearchConnectorConfig) getConnectorConfig();
    }


}
