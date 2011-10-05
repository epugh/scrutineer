package com.aconex.scrutineer.elasticsearch;

import com.aconex.scrutineer.IdAndVersion;
import com.fasterxml.sort.Sorter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ElasticSearchSorterTest {

    @Mock
    private Sorter<IdAndVersion> sorter;
    @Mock
    private InputStream inputstream;
    @Mock
    private OutputStream outputstream;

    @Before public void setup() {
        initMocks(this);
    }

    @Test public void shouldSortInputStream() throws IOException {
        ElasticSearchSorter elasticSearchSorter = new ElasticSearchSorter(sorter);
        elasticSearchSorter.sort(inputstream,outputstream);
        verify(sorter).sort(inputstream, outputstream);
    }
}
