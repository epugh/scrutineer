package com.aconex.scrutineer.elasticsearch;

import com.aconex.scrutineer.IdAndVersion;
import com.fasterxml.sort.DataWriter;

import java.io.IOException;
import java.io.ObjectOutputStream;

class IdAndVersionDataWriter extends DataWriter<IdAndVersion> {
    private final ObjectOutputStream objectOutputStream;

    public IdAndVersionDataWriter(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
    }

    @Override
    public void writeEntry(IdAndVersion item) throws IOException {
        objectOutputStream.writeLong(item.getId());
        objectOutputStream.writeLong(item.getVersion());
    }

    @Override
    public void close() throws IOException {
        objectOutputStream.close();
    }
}
