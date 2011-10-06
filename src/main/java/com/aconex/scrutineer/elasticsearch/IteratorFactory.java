package com.aconex.scrutineer.elasticsearch;

import com.aconex.scrutineer.IdAndVersion;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;

public class IteratorFactory {
    public Iterator<IdAndVersion> forFile(File file) {
        try {
            return new IdAndVersionInputStreamIterator(new IdAndVersionDataReader(
                    new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
