package com.aconex.scrutineer.elasticsearch;

import com.aconex.scrutineer.IdAndVersion;
import com.fasterxml.sort.DataWriter;
import com.fasterxml.sort.DataWriterFactory;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

class IdAndVersionDataWriterFactory extends DataWriterFactory<IdAndVersion> {
    @Override
    public DataWriter<IdAndVersion> constructWriter(OutputStream outputStream) throws IOException {
        return new IdAndVersionDataWriter(new ObjectOutputStream(outputStream));
    }
}