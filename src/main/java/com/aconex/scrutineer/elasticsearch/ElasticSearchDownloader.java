package com.aconex.scrutineer.elasticsearch;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.action.search.SearchRequestBuilder;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;

public class ElasticSearchDownloader {

    private static final int BATCH_SIZE = 100000;
    private static final int SCROLL_TIME_IN_MINUTES = 10;

    private final Client client;
    private final String indexName;

    public ElasticSearchDownloader(Client client, String indexName) {
        this.client = client;
        this.indexName = indexName;
    }

    public void downloadTo(OutputStream outputStream) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            consumeBatches(objectOutputStream, startScrollAndGetFirstBatch());
            objectOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void consumeBatches(ObjectOutputStream objectOutputStream, SearchResponse searchResponse) throws IOException {
        String scrollId = searchResponse.getScrollId();
        do {
            searchResponse = client.prepareSearchScroll(scrollId).setScroll(TimeValue.timeValueMinutes(SCROLL_TIME_IN_MINUTES)).execute().actionGet();
        } while (writeSearchResponseToOutputStream(objectOutputStream, searchResponse));
    }

    boolean writeSearchResponseToOutputStream(ObjectOutputStream objectOutputStream, SearchResponse searchResponse) throws IOException {
        SearchHit[] hits = searchResponse.getHits().hits();
        for (SearchHit hit : hits) {
            objectOutputStream.writeUTF(hit.getId());
            objectOutputStream.writeLong(hit.getVersion());
        }
        return hits.length > 0;
    }

    SearchResponse startScrollAndGetFirstBatch() {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName);
        searchRequestBuilder.setSearchType(SearchType.SCAN);
        searchRequestBuilder.setQuery(matchAllQuery());
        searchRequestBuilder.setSize(3);
        searchRequestBuilder.setExplain(false);
        searchRequestBuilder.setNoFields();
        searchRequestBuilder.setVersion(true);
        searchRequestBuilder.setScroll(TimeValue.timeValueMinutes(SCROLL_TIME_IN_MINUTES));

        return searchRequestBuilder.execute().actionGet();
    }

}
