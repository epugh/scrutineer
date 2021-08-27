package com.aconex.scrutineer2.elasticsearch;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.aconex.scrutineer2.IdAndVersion;
import com.fasterxml.sort.DataWriter;
import com.fasterxml.sort.DataWriterFactory;

public class IdAndVersionDataWriterFactory extends DataWriterFactory<IdAndVersion> {
    @Override
    public DataWriter<IdAndVersion> constructWriter(OutputStream outputStream) throws IOException {
        return new IdAndVersionDataWriter(new ObjectOutputStream(outputStream));
    }
}