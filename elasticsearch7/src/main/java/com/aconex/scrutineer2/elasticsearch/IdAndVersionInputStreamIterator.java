package com.aconex.scrutineer2.elasticsearch;

import java.io.IOException;
import java.util.Iterator;

import com.aconex.scrutineer2.IdAndVersion;

public class IdAndVersionInputStreamIterator implements Iterator<IdAndVersion> {

    private final IdAndVersionDataReader idAndVersionDataReader;
    private IdAndVersion currentValue;

    public IdAndVersionInputStreamIterator(IdAndVersionDataReader idAndVersionDataReader) {
        try {
            this.idAndVersionDataReader = idAndVersionDataReader;
            this.currentValue = idAndVersionDataReader.readNext();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasNext() {
        return currentValue != null;
    }

    @Override
    public IdAndVersion next() {
        try {
            IdAndVersion result = currentValue;
            currentValue = idAndVersionDataReader.readNext();
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
