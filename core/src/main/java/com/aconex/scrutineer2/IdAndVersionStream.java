package com.aconex.scrutineer2;

import java.util.Iterator;

public interface IdAndVersionStream {

    void open();

    Iterator<IdAndVersion> iterator();

    void close();
}
