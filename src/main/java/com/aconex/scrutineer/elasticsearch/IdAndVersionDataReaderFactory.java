package com.aconex.scrutineer.elasticsearch;

import com.aconex.scrutineer.IdAndVersion;
import com.fasterxml.sort.DataReader;
import com.fasterxml.sort.DataReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

class IdAndVersionDataReaderFactory extends DataReaderFactory<IdAndVersion> {
    @Override
    public DataReader<IdAndVersion> constructReader(final InputStream inputStream) throws IOException {
        return new IdAndVersionDataReader(new ObjectInputStream(inputStream));

    }
}
