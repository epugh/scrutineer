package com.aconex.scrutineer2.elasticsearch.v7;

import static com.aconex.scrutineer2.elasticsearch.v7.ElasticSearchDownloader.BATCH_SIZE;
import static com.aconex.scrutineer2.elasticsearch.v7.ElasticSearchDownloader.SCROLL_TIME_IN_MINUTES;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.ObjectOutputStream;

import com.aconex.scrutineer2.IdAndVersionFactory;
import com.aconex.scrutineer2.StringIdAndVersion;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
//@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class ElasticSearchDownloaderTest {

    private static final String INDEX_NAME = "indexName";
    private static final String ID = "123";
    private static final long VERSION = 123L;
    private static final String QUERY = "*";
    private static final String DUMMY_SCROLL_ID = "dummyScrollId";

    private final IdAndVersionFactory idAndVersionFactory = StringIdAndVersion.FACTORY;
    @Mock
    private Client client;

    @Mock(answer = Answers.RETURNS_SELF)
    private SearchRequestBuilder searchRequestBuilder;

    @Mock
    private SearchScrollRequestBuilder searchScrollRequestBuilder;
    @Mock
    private ListenableActionFuture listenableActionFuture;
    @Mock
    private SearchHits hits;
    @Mock
    private SearchHit hit;
    @Mock
    private SearchResponse searchResponse;
    @Mock
    private ObjectOutputStream objectOutputStream;
    @Mock
    private QueryStringQueryBuilder queryBuilder;



    @Test
    public void shouldEndAfterOnlyOneBatch() throws IOException {
        ElasticSearchDownloader elasticSearchDownloader = spy(new ElasticSearchDownloader(client, INDEX_NAME, QUERY, idAndVersionFactory));
        doReturn(false).when(elasticSearchDownloader).writeSearchResponseToOutputStream(any(ObjectOutputStream.class),any(SearchResponse.class));
        when(client.prepareSearch(any(String.class))).thenReturn(searchRequestBuilder);
        when(client.prepareSearchScroll(any(String.class))).thenReturn(searchScrollRequestBuilder);
        when(searchScrollRequestBuilder.execute()).thenReturn(listenableActionFuture);
        when(searchScrollRequestBuilder.setScroll(any(TimeValue.class))).thenReturn(searchScrollRequestBuilder);
        when(listenableActionFuture.actionGet()).thenReturn(searchResponse);
        elasticSearchDownloader.consumeBatches(mock(ObjectOutputStream.class), searchResponse);
        verify(client, never()).prepareSearchScroll(any(String.class)); // one batch shouldn't bother calling for next scroll
        verify(elasticSearchDownloader).writeSearchResponseToOutputStream(any(ObjectOutputStream.class), any(SearchResponse.class));
    }

    @Test
    public void shouldRequestAndProcessNextBatch() throws IOException {
        ElasticSearchDownloader elasticSearchDownloader = spy(new ElasticSearchDownloader(client, INDEX_NAME, QUERY, idAndVersionFactory));
        doReturn(true).doReturn(false).when(elasticSearchDownloader).writeSearchResponseToOutputStream(any(ObjectOutputStream.class),any(SearchResponse.class));
        when(client.prepareSearch(any(String.class))).thenReturn(searchRequestBuilder);
        when(client.prepareSearchScroll(any(String.class))).thenReturn(searchScrollRequestBuilder);
        when(searchScrollRequestBuilder.execute()).thenReturn(listenableActionFuture);
        when(searchScrollRequestBuilder.setScroll(any(TimeValue.class))).thenReturn(searchScrollRequestBuilder);
        when(listenableActionFuture.actionGet()).thenReturn(searchResponse);
        when(searchResponse.getScrollId()).thenReturn(DUMMY_SCROLL_ID);
        elasticSearchDownloader.consumeBatches(mock(ObjectOutputStream.class), searchResponse);
        // even with multiple batches, the prepareSearchScroll is only called once after the initial request
        verify(client,times(1)).prepareSearchScroll(any(String.class));
        verify(searchResponse, times(1)).getScrollId();

        //however we want multiple callso to the write method
        verify(elasticSearchDownloader, times(2)).writeSearchResponseToOutputStream(any(ObjectOutputStream.class), any(SearchResponse.class));
    }


    @Test
    public void shouldShouldReturnFalseWhenBatchIsEmpty() throws IOException {
        ElasticSearchDownloader elasticSearchDownloader = new ElasticSearchDownloader(client, INDEX_NAME, QUERY, idAndVersionFactory);
        when(searchResponse.getHits()).thenReturn(hits);
        when(hits.getHits()).thenReturn(new SearchHit[0]);
        assertThat(elasticSearchDownloader.writeSearchResponseToOutputStream(objectOutputStream, searchResponse), is(false));
    }
    @Test
    public void shouldWriteHitsToOutputStream() throws IOException {
        ElasticSearchDownloader elasticSearchDownloader = new ElasticSearchDownloader(client, INDEX_NAME, QUERY, idAndVersionFactory);
        when(searchResponse.getHits()).thenReturn(hits);
        when(hits.getHits()).thenReturn(new SearchHit[]{hit});
        when(hit.getId()).thenReturn(ID);
        when(hit.getVersion()).thenReturn(VERSION);
        assertThat(elasticSearchDownloader.writeSearchResponseToOutputStream(objectOutputStream, searchResponse), is(true));
        verify(objectOutputStream).writeUTF(ID);
        verify(objectOutputStream).writeLong(VERSION);
        verifyNoMoreInteractions(objectOutputStream);
    }

    @Test
    public void shouldDoElasticSearchRequest() {
        when(client.prepareSearch(INDEX_NAME)).thenReturn(searchRequestBuilder);
        when(searchRequestBuilder.execute()).thenReturn(listenableActionFuture);
        when(listenableActionFuture.actionGet()).thenReturn(searchResponse);
        ElasticSearchDownloader elasticSearchDownloader = spy(new ElasticSearchDownloader(client, INDEX_NAME, QUERY, idAndVersionFactory));
        doReturn(queryBuilder).when(elasticSearchDownloader).createQuery();
        assertThat(elasticSearchDownloader.startScroll(), is(searchResponse));
        verify(searchRequestBuilder).addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC);
        verify(searchRequestBuilder).setFetchSource(false);
        verify(searchRequestBuilder).setVersion(true);
        verify(searchRequestBuilder).setSize(BATCH_SIZE);
        verify(searchRequestBuilder).setScroll(TimeValue.timeValueMinutes(SCROLL_TIME_IN_MINUTES));
        verify(searchRequestBuilder).setQuery(queryBuilder);
    }

    @Test
    public void shouldCreateQueryBuilderWithQuery() {
        ElasticSearchDownloader elasticSearchDownloader = spy(new ElasticSearchDownloader(client, INDEX_NAME, QUERY, idAndVersionFactory));
        assertThat(elasticSearchDownloader.createQuery().toString(), containsString(QUERY));
    }

    @Test
    public void shouldCreateQueryBuilderWithDefaultAllField() {
        ElasticSearchDownloader elasticSearchDownloader = spy(new ElasticSearchDownloader(client, INDEX_NAME, QUERY, idAndVersionFactory));
        assertThat(elasticSearchDownloader.createQuery().toString(), containsString("*"));
    }

    @Test
    public void shouldCreateQueryBuilderWithDefaultAndOperator() {
        ElasticSearchDownloader elasticSearchDownloader = spy(new ElasticSearchDownloader(client, INDEX_NAME, QUERY, idAndVersionFactory));
        assertThat(elasticSearchDownloader.createQuery().toString(), containsString("and"));
    }
}
