package com.aconex.scrutineer.javautil;

import com.aconex.scrutineer.IdAndVersion;
import com.aconex.scrutineer.IdAndVersionStream;

import java.util.Iterator;

public class JavaIteratorIdAndVersionStream implements IdAndVersionStream
{
    private final Iterator<IdAndVersion> iterator;

    public JavaIteratorIdAndVersionStream(Iterator<IdAndVersion> iterator) {
        this.iterator = iterator;
    }

    @Override
    public void open() {
        //Empty
    }

    @Override
    public Iterator<IdAndVersion> iterator() {
        return this.iterator;
    }

    @Override
    public void close() {
        //Empty
    }
}
