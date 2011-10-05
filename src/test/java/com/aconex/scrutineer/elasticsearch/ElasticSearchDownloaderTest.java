package com.aconex.scrutineer.elasticsearch;

import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.action.search.SearchRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ElasticSearchDownloaderTest {

    private static final String INDEX_NAME = "indexName";
    private static final String ID = "123";
    private static final long VERSION = 123L;
    @Mock
    private TransportClient client;
    @Mock
    private SearchRequestBuilder searchRequestBuilder;

    @Mock @SuppressWarnings("unchecked")
    private ListenableActionFuture listenableActionFuture;
    @Mock
    private SearchResponse searchResponse;
    @Mock
    private SearchHits hits;
    @Mock
    private SearchHit searcHit;

    private ByteArrayOutputStream outputStream;

    @Before public void setup() {
        initMocks(this);
        outputStream = new ByteArrayOutputStream();
    }

    @Test public void shouldIterateOverResultsAndSendToOutputStream() throws IOException {
        ElasticSearchDownloader elasticSearchDownloader = spy(new ElasticSearchDownloader(client, INDEX_NAME));
        doReturn(searchResponse).when(elasticSearchDownloader).doSearch();

        when(searchResponse.getHits()).thenReturn(hits);
        when(hits.hits()).thenReturn(new SearchHit[]{searcHit});
        when(searcHit.getId()).thenReturn(ID);
        when(searcHit.getVersion()).thenReturn(VERSION);

        elasticSearchDownloader.downloadTo(outputStream);

        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
        assertThat(objectInputStream.readLong(), is(Long.valueOf(ID)));
        assertThat(objectInputStream.readLong(), is(VERSION));
    }

    @SuppressWarnings("unchecked")
    @Test public void shouldDoElasticSearchRequest() {
        when(client.prepareSearch(INDEX_NAME)).thenReturn(searchRequestBuilder);
        when(searchRequestBuilder.execute()).thenReturn(listenableActionFuture);
        when(listenableActionFuture.actionGet()).thenReturn(searchResponse);
        ElasticSearchDownloader elasticSearchDownloader = new ElasticSearchDownloader(client, INDEX_NAME);
        assertThat(elasticSearchDownloader.doSearch(), is(searchResponse));
        verify(searchRequestBuilder).setSearchType(SearchType.SCAN);
        verify(searchRequestBuilder).setNoFields();
        verify(searchRequestBuilder).setVersion(true);
    }

}
