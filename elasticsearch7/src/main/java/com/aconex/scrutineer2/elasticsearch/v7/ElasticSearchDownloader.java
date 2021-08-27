package com.aconex.scrutineer2.elasticsearch.v7;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.aconex.scrutineer2.IdAndVersionFactory;
import com.aconex.scrutineer2.LogUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;

public class ElasticSearchDownloader {

    private static final Logger LOG = LogUtils.loggerForThisClass();

    static final int BATCH_SIZE = 10000;
    static final int SCROLL_TIME_IN_MINUTES = 10;
    private long numItems = 0;

    private final Client client;
    private final String indexName;
    private final String query;
	private final IdAndVersionFactory idAndVersionFactory;

    public ElasticSearchDownloader(Client client, String indexName, String query, IdAndVersionFactory idAndVersionFactory) {
        this.client = client;
        this.indexName = indexName;
        this.query = query;
        this.idAndVersionFactory = idAndVersionFactory;
    }

    public void downloadTo(OutputStream outputStream) {
        long begin = System.currentTimeMillis();
        doDownloadTo(outputStream);
        LogUtils.infoTimeTaken(LOG, begin, numItems, "Scan & Download completed");
    }

    private void doDownloadTo(OutputStream outputStream) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            consumeBatches(objectOutputStream, startScroll());
            objectOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void consumeBatches(ObjectOutputStream objectOutputStream, SearchResponse initialSearchResponse) throws IOException {
        SearchResponse batchSearchResponse = initialSearchResponse;
        while (writeSearchResponseToOutputStream(objectOutputStream, batchSearchResponse)) {
            String scrollId = batchSearchResponse.getScrollId();
            batchSearchResponse = client.prepareSearchScroll(scrollId).setScroll(TimeValue.timeValueMinutes(SCROLL_TIME_IN_MINUTES)).execute().actionGet();
        }
    }

    boolean writeSearchResponseToOutputStream(ObjectOutputStream objectOutputStream, SearchResponse searchResponse) throws IOException {
        SearchHit[] hits = searchResponse.getHits().getHits();
        enumerateHits(objectOutputStream, hits);
        return hits.length > 0;
    }

    private void enumerateHits(ObjectOutputStream objectOutputStream, SearchHit[] hits) throws IOException {
        for (SearchHit hit : hits) {
        	idAndVersionFactory.create(hit.getId(), hit.getVersion()).writeToStream(objectOutputStream);
            numItems++;
        }
    }

    QueryStringQueryBuilder createQuery() {
        return QueryBuilders.queryStringQuery(query).defaultOperator(Operator.AND).defaultField("*");
    }

    @SuppressWarnings("PMD.NcssMethodCount")
    SearchResponse startScroll() {

        //https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-search-scrolling.html
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName);

        searchRequestBuilder.addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
        .setQuery(createQuery())
        .setSize(BATCH_SIZE)
        .setExplain(false)
        .setFetchSource(false)
        .setVersion(true)
        .setScroll(TimeValue.timeValueMinutes(SCROLL_TIME_IN_MINUTES));

        return searchRequestBuilder.execute().actionGet();
    }

}
