package com.aconex.scrutineer;

import java.util.Iterator;

public interface IdAndVersionStream {

    void open();

    Iterator<IdAndVersion> iterator();

    void close();
}
