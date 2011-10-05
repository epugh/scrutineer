package com.aconex.scrutineer.elasticsearch;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.action.search.SearchRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

public class ElasticSearchDownloader {

    private static final int BATCH_SIZE = 100000;

    private final TransportClient client;
    private final String indexName;

    public ElasticSearchDownloader(TransportClient client, String indexName) {
        this.client = client;
        this.indexName = indexName;
    }

    public void downloadTo(OutputStream outputStream) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            for(SearchHit hit: doSearch().getHits().hits()) {
                objectOutputStream.writeLong(Long.valueOf(hit.getId()));
                objectOutputStream.writeLong(hit.getVersion());
            }
            objectOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SearchResponse doSearch() {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName);
        searchRequestBuilder.setSearchType(SearchType.SCAN);
        searchRequestBuilder.setQuery(matchAllQuery());
        searchRequestBuilder.setSize(BATCH_SIZE);
        searchRequestBuilder.setExplain(false);
        searchRequestBuilder.setNoFields();
        searchRequestBuilder.setVersion(true);
        searchRequestBuilder.setScroll(TimeValue.timeValueMinutes(10));
        return searchRequestBuilder.execute().actionGet();
    }
}
