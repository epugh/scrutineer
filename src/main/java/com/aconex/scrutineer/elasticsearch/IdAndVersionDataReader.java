package com.aconex.scrutineer.elasticsearch;

import com.aconex.scrutineer.IdAndVersion;
import com.fasterxml.sort.DataReader;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;

class IdAndVersionDataReader extends DataReader<IdAndVersion> {

    private static final int ESTIMATED_SIZE = 2 * 8;
    
    private final ObjectInputStream objectInputStream;

    public IdAndVersionDataReader(ObjectInputStream objectInputStream) {
        this.objectInputStream = objectInputStream;
    }

    @Override
    public IdAndVersion readNext() throws IOException {
        try {
            return new IdAndVersion(objectInputStream.readLong(), objectInputStream.readLong());
        } catch (EOFException e) {
            return null;
        }
    }

    @Override
    public int estimateSizeInBytes(IdAndVersion item) {
        return ESTIMATED_SIZE;
    }

    @Override
    public void close() throws IOException {
        objectInputStream.close();
    }
}
