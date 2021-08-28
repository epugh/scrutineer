package com.aconex.scrutineer2.elasticsearch;

import com.aconex.scrutineer2.IdAndVersion;
import com.aconex.scrutineer2.IdAndVersionFactory;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

public class IdAndVersionElasticSearchScrollResultIterator implements Iterator<IdAndVersion> {
    static final int SCROLL_TIME_IN_MINUTES = 10;
    private final Client client;
    private final IdAndVersionFactory idAndVersionFactory;
    private SearchHit[] hits;
    private int currentHitIndex = 0;
    private final String scrollId;

    IdAndVersionElasticSearchScrollResultIterator(Client client, SearchResponse initialSearchResponse, IdAndVersionFactory idAndVersionFactory) {
        this.client = client;
        scrollId = initialSearchResponse.getScrollId();
        this.idAndVersionFactory = idAndVersionFactory;
        extractHits(initialSearchResponse);
    }

    @Override
    public boolean hasNext() {
        if (currentHitIndex >= hits.length && !scrollToNextBatch()) {
            hits=null;
            return false;
        }
        return hits != null;
    }

    @Override
    public IdAndVersion next() {
        checkNotNull(hits, "Internal error: SearchHit collection is null.");
        IdAndVersion idAndVersion = idAndVersionFactory.create(hits[currentHitIndex].getId(), hits[currentHitIndex].getVersion());
        currentHitIndex++;
        return idAndVersion;
    }

    private boolean scrollToNextBatch() {
        SearchResponse batchSearchResponse = scroll(client, scrollId);
        extractHits(batchSearchResponse);
        return hasScrollFinished();
    }

    private SearchResponse scroll(Client client, String scrollId) {
        return client.prepareSearchScroll(scrollId)
                .setScroll(TimeValue.timeValueMinutes(SCROLL_TIME_IN_MINUTES))
                .execute()
                .actionGet();
    }

    private void extractHits(SearchResponse searchResponse) {
        hits = searchResponse.getHits().getHits();
        currentHitIndex = 0;
    }

    private boolean hasScrollFinished() {
        if (hits.length == 0) {
            hits = null;
            return false;
        }
        return true;
    }
}
