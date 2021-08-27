package com.aconex.scrutineer2.elasticsearch;

import java.io.IOException;
import java.io.ObjectOutputStream;

import com.aconex.scrutineer2.IdAndVersion;
import com.fasterxml.sort.DataWriter;

class IdAndVersionDataWriter extends DataWriter<IdAndVersion> {
    private final ObjectOutputStream objectOutputStream;

    IdAndVersionDataWriter(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
    }

    @Override
    public void writeEntry(IdAndVersion item) throws IOException {
        item.writeToStream(objectOutputStream);
    }

    @Override
    public void close() throws IOException {
        objectOutputStream.close();
    }
}
