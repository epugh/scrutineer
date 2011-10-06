package com.aconex.scrutineer.elasticsearch;

import java.io.IOException;
import java.io.ObjectOutputStream;

import com.aconex.scrutineer.IdAndVersion;
import com.fasterxml.sort.DataWriter;

class IdAndVersionDataWriter extends DataWriter<IdAndVersion> {
    private final ObjectOutputStream objectOutputStream;

    public IdAndVersionDataWriter(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
    }

    @Override
    public void writeEntry(IdAndVersion item) throws IOException {
        objectOutputStream.writeUTF(item.getId());
        objectOutputStream.writeLong(item.getVersion());
    }

    @Override
    public void close() throws IOException {
        objectOutputStream.close();
    }
}
