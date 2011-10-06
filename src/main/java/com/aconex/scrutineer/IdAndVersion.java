package com.aconex.scrutineer;

import org.apache.commons.lang.builder.CompareToBuilder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class IdAndVersion implements Comparable<IdAndVersion> {

    private final String id;
    private final long version;

    public IdAndVersion(String id, long version) {
        this.id = id;
        this.version = version;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IdAndVersion)) {
            return false;
        }
        IdAndVersion other = (IdAndVersion) obj;
        return this.compareTo(other) == 0;
    }

    @Override
    public int hashCode() {
        return 7 +
           id.hashCode() +
           (int)(version ^ (version >>> 32));
    }

    @Override
    public String toString() {
        return id + ":" + version;
    }

    public int compareTo(IdAndVersion other) {
        return new CompareToBuilder().append(id, other.id).append(version, other.version).toComparison();
    }

    public String getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }

    public void writeToStream(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeUTF(id);
        objectOutputStream.writeLong(version);
    }

    public static IdAndVersion readFromStream(ObjectInputStream inputStream) throws IOException {
        return new IdAndVersion(inputStream.readUTF(), inputStream.readLong());
    }
}
