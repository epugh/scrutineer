package com.aconex.scrutineer2;

import java.io.IOException;
import java.io.ObjectInputStream;

public interface IdAndVersionFactory {
	IdAndVersion create(Object id, long version);
	IdAndVersion readFromStream(ObjectInputStream inputStream) throws IOException;
}
