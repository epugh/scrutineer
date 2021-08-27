package com.aconex.scrutineer2.elasticsearch;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Iterator;

import com.aconex.scrutineer2.IdAndVersion;
import com.aconex.scrutineer2.IdAndVersionFactory;
import com.aconex.scrutineer2.StringIdAndVersion;
import com.aconex.scrutineer2.elasticsearch.v7.ElasticSearchDownloader;
import com.fasterxml.sort.DataReaderFactory;
import com.fasterxml.sort.DataWriterFactory;
import com.fasterxml.sort.SortConfig;
import com.fasterxml.sort.Sorter;
import com.fasterxml.sort.util.NaturalComparator;
import org.apache.commons.lang3.SystemUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ElasticSearchIdAndVersionStreamIntegrationTest {

    private static final String INDEX_NAME = "local";
	private final IdAndVersionFactory idAndVersionFactory = StringIdAndVersion.FACTORY;
    private Client client;
    private ElasticSearchTestHelper elasticSearchTestHelper;

    @Before
    public void setup() throws NodeValidationException {
        Node node = ESIntegrationTestNode.elasticSearchTestNode();
        client = node.client();
        deleteIndexIfExists();

        indexIdAndVersion("1", 1);
        indexIdAndVersion("3", 3);
        indexIdAndVersion("2", 2);

        // make explicit _refresh call to make documents visible
        client.admin().indices().prepareRefresh(INDEX_NAME).execute().actionGet();
    }

    @After
    public void teardown() {
        client.close();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldGetStreamFromElasticSearch() {

        SortConfig sortConfig = new SortConfig().withMaxMemoryUsage(256*1024*1024);
        DataReaderFactory<IdAndVersion> dataReaderFactory = new IdAndVersionDataReaderFactory(idAndVersionFactory);
        DataWriterFactory<IdAndVersion> dataWriterFactory = new IdAndVersionDataWriterFactory();
        Sorter sorter = new Sorter(sortConfig, dataReaderFactory, dataWriterFactory, new NaturalComparator<IdAndVersion>());
        ElasticSearchDownloader elasticSearchDownloader = new ElasticSearchDownloader(client, INDEX_NAME, "*", idAndVersionFactory);
        ElasticSearchIdAndVersionStream elasticSearchIdAndVersionStream =
                new ElasticSearchIdAndVersionStream(elasticSearchDownloader, new ElasticSearchSorter(sorter), new IteratorFactory(idAndVersionFactory), SystemUtils.getJavaIoTmpDir().getAbsolutePath());

        elasticSearchIdAndVersionStream.open();
        Iterator<IdAndVersion> iterator = elasticSearchIdAndVersionStream.iterator();

        assertThat(iterator.next(), equalTo(new StringIdAndVersion("1",1)));
        assertThat(iterator.next(), equalTo(new StringIdAndVersion("2",2)));
        assertThat(iterator.next(), equalTo(new StringIdAndVersion("3",3)));

        elasticSearchIdAndVersionStream.close();
    }

    private void deleteIndexIfExists() {
        elasticSearchTestHelper = new ElasticSearchTestHelper(client);
        elasticSearchTestHelper.deleteIndexIfItExists(INDEX_NAME);
    }

    private void indexIdAndVersion(String id, long version) {
        client.prepareIndex().setIndex(INDEX_NAME).setId(id).setVersion(version).setVersionType(VersionType.EXTERNAL).setSource("value", 1).execute().actionGet();
    }

}
