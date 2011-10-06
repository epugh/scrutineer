package com.aconex.scrutineer.elasticsearch;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.aconex.scrutineer.IdAndVersion;
import com.fasterxml.sort.DataReader;

class IdAndVersionDataReader extends DataReader<IdAndVersion> {

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
        return item.getId().length()*2+8;
    }

    @Override
    public void close() throws IOException {
        objectInputStream.close();
    }
}
