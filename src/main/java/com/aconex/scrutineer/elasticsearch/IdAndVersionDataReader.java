package com.aconex.scrutineer.elasticsearch;

import com.aconex.scrutineer.IdAndVersion;
import com.fasterxml.sort.DataReader;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;

class IdAndVersionDataReader extends DataReader<IdAndVersion> {

    private static final int BYTES_PER_CHAR = 2;
    private static final int BYTES_PER_LONG = 8;
    
    private final ObjectInputStream objectInputStream;

    public IdAndVersionDataReader(ObjectInputStream objectInputStream) {
        this.objectInputStream = objectInputStream;
    }

    @Override
    public IdAndVersion readNext() throws IOException {
        try {
            return IdAndVersion.readFromStream(objectInputStream);
        } catch (EOFException e) {
            return null;
        }
    }

    @Override
    public int estimateSizeInBytes(IdAndVersion item) {
        return item.getId().length()* BYTES_PER_CHAR + BYTES_PER_LONG;
    }

    @Override
    public void close() throws IOException {
        objectInputStream.close();
    }
}
