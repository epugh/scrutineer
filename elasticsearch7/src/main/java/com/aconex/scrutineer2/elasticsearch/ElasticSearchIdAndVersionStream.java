package com.aconex.scrutineer2.elasticsearch;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import com.aconex.scrutineer2.IdAndVersion;
import com.aconex.scrutineer2.IdAndVersionStream;
import com.aconex.scrutineer2.elasticsearch.v7.ElasticSearchDownloader;

public class ElasticSearchIdAndVersionStream implements IdAndVersionStream {

    private static final String ELASTIC_SEARCH_UNSORTED_FILE = "elastic-search-unsorted.dat";

    private static final String ELASTIC_SEARCH_SORTED_FILE = "elastic-search-sorted.dat";

    private final ElasticSearchDownloader elasticSearchDownloader;
    private final ElasticSearchSorter elasticSearchSorter;
    private final IteratorFactory iteratorFactory;
    private final File unsortedFile;
    private final File sortedFile;

    public ElasticSearchIdAndVersionStream(ElasticSearchDownloader elasticSearchDownloader, ElasticSearchSorter elasticSearchSorter, IteratorFactory iteratorFactory, String workingDirectory) {
        this.elasticSearchDownloader = elasticSearchDownloader;
        this.elasticSearchSorter = elasticSearchSorter;
        this.iteratorFactory = iteratorFactory;
        unsortedFile = new File(workingDirectory, ELASTIC_SEARCH_UNSORTED_FILE);
        sortedFile = new File(workingDirectory, ELASTIC_SEARCH_SORTED_FILE);
    }

    @Override
    public void open() {
        elasticSearchDownloader.downloadTo(createUnsortedOutputStream());
        elasticSearchSorter.sort(createUnSortedInputStream(), createSortedOutputStream());
    }

    @Override
    public Iterator<IdAndVersion> iterator() {
        return iteratorFactory.forFile(sortedFile);
    }

    @Override
    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public void close() {
        unsortedFile.delete();
        sortedFile.delete();
    }

    OutputStream createUnsortedOutputStream() {
        try {
            return new BufferedOutputStream(new FileOutputStream(unsortedFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    InputStream createUnSortedInputStream() {
        try {
            return new BufferedInputStream(new FileInputStream(unsortedFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    OutputStream createSortedOutputStream() {
        try {
            return new BufferedOutputStream(new FileOutputStream(sortedFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
