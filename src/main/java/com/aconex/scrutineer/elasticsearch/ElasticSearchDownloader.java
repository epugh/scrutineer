package com.aconex.scrutineer.elasticsearch;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.action.search.SearchRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;

public class ElasticSearchDownloader {

    private static final int BATCH_SIZE = 100000;
    private static final int SCROLL_TIME_IN_MINUTES = 10;

    private final TransportClient client;
    private final String indexName;
    private String scrollId;
    private boolean completed;

    public ElasticSearchDownloader(TransportClient client, String indexName) {
        this.client = client;
        this.indexName = indexName;
    }

    public void downloadTo(OutputStream outputStream) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            startScrollAndProcessFirstBatch(objectOutputStream);
            consumeAllOtherBatches(objectOutputStream);

            objectOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void consumeAllOtherBatches(ObjectOutputStream objectOutputStream) throws IOException {
        while (!isCompleted()) {
            SearchResponse searchResponse = nextBatch();
            writeSearchResponseToOutputStream(objectOutputStream, searchResponse);
        }
    }

    private void startScrollAndProcessFirstBatch(ObjectOutputStream objectOutputStream) throws IOException {
        SearchResponse searchResponse = startScroll();
        writeSearchResponseToOutputStream(objectOutputStream, searchResponse);
    }

    boolean isCompleted(){
        return completed;
    }

    private void writeSearchResponseToOutputStream(ObjectOutputStream objectOutputStream, SearchResponse searchResponse) throws IOException {
        for (SearchHit hit : searchResponse.getHits().hits()) {
            objectOutputStream.writeLong(Long.valueOf(hit.getId()));
            objectOutputStream.writeLong(hit.getVersion());
        }
    }

    public SearchResponse startScroll() {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName);
        searchRequestBuilder.setSearchType(SearchType.SCAN);
        searchRequestBuilder.setQuery(matchAllQuery());
        searchRequestBuilder.setSize(BATCH_SIZE);
        searchRequestBuilder.setExplain(false);
        searchRequestBuilder.setNoFields();
        searchRequestBuilder.setVersion(true);
        searchRequestBuilder.setScroll(TimeValue.timeValueMinutes(SCROLL_TIME_IN_MINUTES));
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        scrollId = searchResponse.getScrollId();
        return searchResponse;
    }

    SearchResponse nextBatch() {
        SearchResponse searchResponse = client.prepareSearchScroll(scrollId).setScroll(TimeValue.timeValueMinutes(SCROLL_TIME_IN_MINUTES)).execute().actionGet();
        if (searchResponse.getHits().hits().length == 0) {
            completed = true;
        }
        return searchResponse;
    }
}
