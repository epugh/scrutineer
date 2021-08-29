package com.aconex.scrutineer2.javautil;

import java.util.Iterator;

import com.aconex.scrutineer2.IdAndVersion;
import com.aconex.scrutineer2.IdAndVersionStream;

public class JavaIteratorIdAndVersionStream implements IdAndVersionStream
{
    private final Iterator<IdAndVersion> iterator;

    public JavaIteratorIdAndVersionStream(Iterator<IdAndVersion> iterator) {
        this.iterator = iterator;
    }

    @Override
    public Iterator<IdAndVersion> iterator() {
        return this.iterator;
    }

}
