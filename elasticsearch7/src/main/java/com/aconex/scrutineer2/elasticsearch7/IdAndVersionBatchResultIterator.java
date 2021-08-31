package com.aconex.scrutineer2.elasticsearch;

import com.aconex.scrutineer2.IdAndVersion;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class IdAndVersionBatchResultIterator implements Iterator<IdAndVersion> {
    private final Supplier<Iterator<IdAndVersion>> batchProvider;
    private Iterator<IdAndVersion> batchIterator;

    IdAndVersionBatchResultIterator(Supplier<Iterator<IdAndVersion>> batchProvider, Iterator<IdAndVersion> firstBatchIterator) {
        this.batchProvider = batchProvider;
        batchIterator = firstBatchIterator;
    }

    @Override
    public boolean hasNext() {
        return batchIterator!=null && batchIterator.hasNext();
    }

    @Override
    public IdAndVersion next() {
        if(!hasNext()) {
            getNextBatch();
        }
        if(batchIterator==null){
            throw new NoSuchElementException();
        }
        return batchIterator.next();
    }

    private void getNextBatch() {
        batchIterator = batchProvider.get();
    }
}
