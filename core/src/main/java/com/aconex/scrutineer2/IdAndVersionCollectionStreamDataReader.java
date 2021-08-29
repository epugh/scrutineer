package com.aconex.scrutineer2;

import com.fasterxml.sort.DataReader;

import java.util.Iterator;

public class IdAndVersionCollectionStreamDataReader extends DataReader<IdAndVersion> {
    private static final int BYTES_PER_CHAR = 2;
    private static final int BYTES_PER_LONG = 8;
    private static final int BYTES_PER_OBJECT_POINTER = 24;
    private static final int BYTES_PER_ARRAY_POINTER = 28;
    private final Iterator<IdAndVersion> iterator;

    public IdAndVersionCollectionStreamDataReader(Iterator<IdAndVersion> iterator) {
        this.iterator = iterator;
    }

    @Override
    public IdAndVersion readNext() {
        if(iterator.hasNext()){
            return iterator.next();
        }
        return null;
    }

    @Override
    public int estimateSizeInBytes(IdAndVersion item) {
        int idAndVersionContainerSize = BYTES_PER_OBJECT_POINTER;
        int versionSize = BYTES_PER_LONG;
        int idSize = BYTES_PER_OBJECT_POINTER + BYTES_PER_ARRAY_POINTER + (item.getId().length() * BYTES_PER_CHAR);

        return idAndVersionContainerSize + versionSize + idSize;
    }

    @Override
    public void close() {

    }
}
