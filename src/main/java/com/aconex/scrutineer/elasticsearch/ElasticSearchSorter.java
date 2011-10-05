package com.aconex.scrutineer.elasticsearch;

import com.aconex.scrutineer.IdAndVersion;
import com.fasterxml.sort.Sorter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ElasticSearchSorter {

    private final Sorter<IdAndVersion> sorter;

    public ElasticSearchSorter(Sorter<IdAndVersion> sorter) {
        this.sorter = sorter;
    }

    public void sort(InputStream inputStream, OutputStream outputStream) {
        try {
            sorter.sort(inputStream,outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
